package com.yoga.backend.fcm;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.transaction.annotation.Transactional;

/**
 * fcm 알림 전송 서비스
 * <p>
 * todo 캐시 관리 시간에서 캐시 추가는 23:49, 캐시 삭제는 다음날 자정으로
 * todo 강의 추가 시 캐시되나 알림 실행이 안됨
 */
@Slf4j
@Service
public class NotificationService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final String REDIS_KEY_PREFIX = "yoga:lectures:";

    private final LiveLectureRepository liveLectureRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final FCMService fcmService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UsersRepository usersRepository;

    public NotificationService(LiveLectureRepository liveLectureRepository,
        MyLiveLectureRepository myLiveLectureRepository, FCMService fcmService,
        RedisTemplate<String, Object> redisTemplate, UsersRepository usersRepository) {
        this.liveLectureRepository = liveLectureRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.fcmService = fcmService;
        this.redisTemplate = redisTemplate;
        this.usersRepository = usersRepository;
    }


    /**
     * 강의 업데이트시 redis에 업데이트된 강의 저장
     *
     * @param updatedLecture 업데이트된 강의 정보
     */
    @Transactional
    public void handleLectureUpdate(LiveLectures updatedLecture) {
        LiveLectureDto lectureDTO = LiveLectureDto.fromEntity(updatedLecture);
        LocalDate startDate = lectureDTO.getStartDate().atZone(KOREA_ZONE).toLocalDate();
        LocalDate endDate = lectureDTO.getEndDate().atZone(KOREA_ZONE).toLocalDate();
        LocalTime lectureStartTime = extractTimeFromInstant(lectureDTO.getStartTime());
        Set<DayOfWeek> availableDays = parseAvailableDays(lectureDTO.getAvailableDay());

        log.info("강의 업데이트 시작 - ID: {}, 제목: {}, 시작일: {}, 종료일: {}, 요일: {}",
            lectureDTO.getLiveId(), lectureDTO.getLiveTitle(), startDate, endDate,
            lectureDTO.getAvailableDay());

        LocalDate today = LocalDate.now(KOREA_ZONE);
        boolean updatedToday = false;

        // 강의 기간 동안 각 날짜에 대해
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (availableDays.contains(date.getDayOfWeek())) {
                String redisKey = REDIS_KEY_PREFIX + date.toString();
                List<LiveLectureDto> lectures = getLecturesFromRedis(redisKey);
                boolean found = false;

                // 기존 강의 업데이트 or 새 강의 추가
                for (int i = 0; i < lectures.size(); i++) {
                    if (lectures.get(i).getLiveId().equals(lectureDTO.getLiveId())) {
                        lectures.set(i, lectureDTO);
                        found = true;
                        log.info("기존 강의 업데이트 - 날짜: {}, 강의 ID: {}", date, lectureDTO.getLiveId());
                        break;
                    }
                }
                if (!found) {
                    lectures.add(lectureDTO);
                    log.info("새 강의 추가 - 날짜: {}, 강의 ID: {}", date, lectureDTO.getLiveId());
                }

                // redis에 강의 정보 저장
                redisTemplate.opsForValue().set(redisKey, lectures);
                redisTemplate.expireAt(redisKey,
                    Date.from(date.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant()));
                log.info("강의 ID {} Redis 캐시 업데이트 완료 (날짜: {}, 시작 시간: {})",
                    lectureDTO.getLiveId(), date, lectureStartTime);

                // 오늘 강의라면
                if (date.equals(today)) {
                    updatedToday = true;
                }
            }
        }

        // 오늘 강의라면 스케줄러 실행
        if (updatedToday) {
            log.info("오늘 날짜의 강의가 업데이트되어 스케줄러를 즉시 실행합니다.");
            checkUpcomingLecturesAndNotify();
        }
        log.info("강의 업데이트 완료 - ID: {}", lectureDTO.getLiveId());
    }

    /**
     * 강의가 삭제된 경우
     *
     * @param liveId 삭제할 강의 ID
     */
    @Transactional
    public void handleLectureDelete(Long liveId) {
        log.info("강의 삭제 시작 - ID: {}", liveId);
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        log.info("검색된 Redis 키 수: {}", keys.size());

        // 모든 Redis 키에 대해 강의 삭제
        for (String key : keys) {
            List<Object> lecturesFromRedis = (List<Object>) redisTemplate.opsForValue().get(key);
            if (lecturesFromRedis != null) {

                List<LiveLectureDto> lectures = convertToLiveLectureDtoList(lecturesFromRedis);
                int initialSize = lectures.size();
                lectures.removeIf(lecture -> lecture.getLiveId().equals(liveId));

                if (lectures.size() < initialSize) {
                    redisTemplate.opsForValue().set(key, lectures);
                    log.info("강의 ID {} Redis 캐시에서 삭제 완료 (키: {})", liveId, key);
                }
            }
        }
        log.info("강의 삭제 완료 - ID: {}", liveId);
    }


    /**
     * 매일 23:49에 실행, 내일 할 강의 캐시
     */
    @Scheduled(cron = "0 49 23 * * *")
    @Transactional
    public void cacheTomorrowLectures() {
        try {
            LocalDate tomorrowKorea = LocalDate.now(KOREA_ZONE).plusDays(1);
            String tomorrowDayAbbreviation = tomorrowKorea.getDayOfWeek().toString()
                .substring(0, 3);

            System.out.println("매일 23:49실행 for fcm ");
            System.out.println("tomorrowDayAbbreviation: " + tomorrowDayAbbreviation);
            System.out.println("tomorrowkorea: " + tomorrowKorea);

            List<LiveLectures> tomorrowLectures = liveLectureRepository.findTomorrowLectures(
                tomorrowKorea, tomorrowDayAbbreviation); // 여기서 에러 발생

            /**
             *2024-08-07T23:51:00.034+09:00 ERROR 14504 --- [backend] [   scheduling-1] c.yoga.backend.fcm.NotificationService   : 내일 강의 목록 Redis 캐시 갱신 중 에러 발생
             *
             * org.springframework.dao.InvalidDataAccessApiUsageException: Argument [2024-08-08] of type [java.time.LocalDate] did not match parameter type [java.time.Instant (n/a)]
             * 	at org.springframework.orm.jpa.EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(EntityManagerFactoryUtils.java:371) ~[spring-orm-6.1.10.jar:6.1.10]
             * 	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:246) ~[spring-orm-6.1.10.jar:6.1.10]
             * 	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.translateExceptionIfPossible(AbstractEntityManagerFactoryBean.java:550) ~[spring-orm-6.1.10.jar:6.1.10]
             * 	at org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.dao.support.DataAccessUtils.translateIfNecessary(DataAccessUtils.java:335) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:160) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor.invoke(CrudMethodMetadataPostProcessor.java:136) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:97) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:223) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at jdk.proxy4/jdk.proxy4.$Proxy170.findTomorrowLectures(Unknown Source) ~[na:na]
             * 	at com.yoga.backend.fcm.NotificationService.cacheTomorrowLectures(NotificationService.java:158) ~[classes/:na]
             * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
             * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77) ~[na:na]
             * 	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
             * 	at java.base/java.lang.reflect.Method.invoke(Method.java:568) ~[na:na]
             * 	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:354) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:768) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:123) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:392) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:768) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:720) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at com.yoga.backend.fcm.NotificationService$$SpringCGLIB$$0.cacheTomorrowLectures(<generated>) ~[classes/:na]
             * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
             * 	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77) ~[na:na]
             * 	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
             * 	at java.base/java.lang.reflect.Method.invoke(Method.java:568) ~[na:na]
             * 	at org.springframework.scheduling.support.ScheduledMethodRunnable.runInternal(ScheduledMethodRunnable.java:130) ~[spring-context-6.1.10.jar:6.1.10]
             * 	at org.springframework.scheduling.support.ScheduledMethodRunnable.lambda$run$2(ScheduledMethodRunnable.java:124) ~[spring-context-6.1.10.jar:6.1.10]
             * 	at io.micrometer.observation.Observation.observe(Observation.java:499) ~[micrometer-observation-1.13.1.jar:1.13.1]
             * 	at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:124) ~[spring-context-6.1.10.jar:6.1.10]
             * 	at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54) ~[spring-context-6.1.10.jar:6.1.10]
             * 	at org.springframework.scheduling.concurrent.ReschedulingRunnable.run(ReschedulingRunnable.java:96) ~[spring-context-6.1.10.jar:6.1.10]
             * 	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539) ~[na:na]
             * 	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264) ~[na:na]
             * 	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304) ~[na:na]
             * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136) ~[na:na]
             * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635) ~[na:na]
             * 	at java.base/java.lang.Thread.run(Thread.java:840) ~[na:na]
             * Caused by: org.hibernate.query.QueryArgumentException: Argument [2024-08-08] of type [java.time.LocalDate] did not match parameter type [java.time.Instant (n/a)]
             * 	at org.hibernate.query.spi.QueryParameterBindingValidator.validate(QueryParameterBindingValidator.java:85) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.spi.QueryParameterBindingValidator.validate(QueryParameterBindingValidator.java:32) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.internal.QueryParameterBindingImpl.validate(QueryParameterBindingImpl.java:362) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.internal.QueryParameterBindingImpl.setBindValue(QueryParameterBindingImpl.java:137) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.spi.AbstractCommonQueryContract.setParameter(AbstractCommonQueryContract.java:842) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.spi.AbstractSelectionQuery.setParameter(AbstractSelectionQuery.java:882) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.sqm.internal.QuerySqmImpl.setParameter(QuerySqmImpl.java:1200) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.hibernate.query.sqm.internal.QuerySqmImpl.setParameter(QuerySqmImpl.java:136) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
             * 	at org.springframework.data.jpa.repository.query.QueryParameterSetter$BindableQuery.setParameter(QueryParameterSetter.java:326) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.QueryParameterSetter$NamedOrIndexedQueryParameterSetter.lambda$4(QueryParameterSetter.java:117) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.QueryParameterSetter$ErrorHandling$1.execute(QueryParameterSetter.java:140) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.QueryParameterSetter$NamedOrIndexedQueryParameterSetter.setParameter(QueryParameterSetter.java:117) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.ParameterBinder.bind(ParameterBinder.java:84) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.ParameterBinder.bind(ParameterBinder.java:76) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.ParameterBinder.bindAndPrepare(ParameterBinder.java:98) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.AbstractStringBasedJpaQuery.doCreateQuery(AbstractStringBasedJpaQuery.java:130) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.AbstractJpaQuery.createQuery(AbstractJpaQuery.java:243) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.JpaQueryExecution$CollectionExecution.doExecute(JpaQueryExecution.java:129) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.JpaQueryExecution.execute(JpaQueryExecution.java:92) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.AbstractJpaQuery.doExecute(AbstractJpaQuery.java:152) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.jpa.repository.query.AbstractJpaQuery.execute(AbstractJpaQuery.java:140) ~[spring-data-jpa-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.repository.core.support.RepositoryMethodInvoker.doInvoke(RepositoryMethodInvoker.java:170) ~[spring-data-commons-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.repository.core.support.RepositoryMethodInvoker.invoke(RepositoryMethodInvoker.java:158) ~[spring-data-commons-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.doInvoke(QueryExecutorMethodInterceptor.java:164) ~[spring-data-commons-3.3.1.jar:3.3.1]
             * 	at org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.invoke(QueryExecutorMethodInterceptor.java:143) ~[spring-data-commons-3.3.1.jar:3.3.1]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.data.projection.DefaultMethodInvokingMethodInterceptor.invoke(DefaultMethodInvokingMethodInterceptor.java:70) ~[spring-data-commons-3.3.1.jar:3.3.1]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:123) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:392) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184) ~[spring-aop-6.1.10.jar:6.1.10]
             * 	at org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:138) ~[spring-tx-6.1.10.jar:6.1.10]
             * 	... 39 common frames omitted
             */

            if (!tomorrowLectures.isEmpty()) {
                String redisKey = REDIS_KEY_PREFIX + tomorrowKorea.toString();
                List<LiveLectureDto> tomorrowLectureDtos = tomorrowLectures.stream()
                    .map(LiveLectureDto::fromEntity)
                    .collect(Collectors.toList());

                redisTemplate.opsForValue().set(redisKey, tomorrowLectureDtos);
                redisTemplate.expireAt(redisKey,
                    Date.from(tomorrowKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant()));
                log.info("내일 강의 목록 Redis 캐시 갱신 완료. 강의 개수: {}", tomorrowLectureDtos.size());
            } else {
                log.info("내일 예정된 강의가 없습니다.");
            }
        } catch (Exception e) {
            log.error("내일 강의 목록 Redis 캐시 갱신 중 에러 발생", e);
        }
    }

    /**
     * 매일 00:05에 실행, 만료된 캐시 삭제
     */
    @Scheduled(cron = "0 5 0 * * *")
    public void cleanYesterdayCache() {
        try {
            LocalDate yesterday = LocalDate.now(KOREA_ZONE).minusDays(1);
            String redisKey = REDIS_KEY_PREFIX + yesterday.toString();

            Boolean deleted = redisTemplate.delete(redisKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("전날({})의 캐시 삭제 완료", yesterday);
            } else {
                log.warn("전날({})의 캐시가 존재하지 않거나 삭제 실패", yesterday);
            }
        } catch (Exception e) {
            log.error("전날 캐시 삭제 중 에러 발생: {}", e.getMessage());
        }
    }

    /**
     * 1분마다 실행, 예정된 강의를 확인 및 알림 전송
     */
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void checkUpcomingLecturesAndNotify() {
        try {
            ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
            LocalDate todayKorea = nowKorea.toLocalDate();
            LocalTime nowTimeKorea = nowKorea.toLocalTime();
            String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);

            System.out.println("1분마다 실행 for fcm");
            System.out.println("nowkorea: " + nowKorea);
            System.out.println("todayKorea: " + todayKorea);
            System.out.println("nowTimeKorea: " + nowTimeKorea);

            String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
            List<LiveLectureDto> todayLectures = getLecturesFromRedis(redisKey);

            if (todayLectures.isEmpty()) {
                log.warn("Redis에서 오늘의 강의 목록 조회 불가. DB에서 조회.");
                todayLectures = liveLectureRepository.findLecturesForToday(todayKorea,
                        dayAbbreviation)
                    .stream()
                    .map(LiveLectureDto::fromEntity)
                    .collect(Collectors.toList());

                redisTemplate.opsForValue().set(redisKey, todayLectures);
                redisTemplate.expireAt(redisKey,
                    Date.from(todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant()));
            }

            log.info("오늘 강의 목록 확인 완료. 강의 개수: {}", todayLectures.size());

            // 10분 후에 시작하는 강의 찾기
            LocalTime tenMinutesLater = nowTimeKorea.plusMinutes(10);
            System.out.println("tenMinutesLater: " + tenMinutesLater);
            List<LiveLectureDto> upcomingLectures = todayLectures.stream()
                .filter(lecture -> {
                    LocalTime lectureStartTime = LocalTime.ofInstant(lecture.getStartTime(),
                        KOREA_ZONE);
                    return lectureStartTime.isAfter(nowTimeKorea) &&
                        lectureStartTime.isBefore(tenMinutesLater.plusSeconds(30)) &&
                        lectureStartTime.isAfter(tenMinutesLater.minusSeconds(30));
                })
                .collect(Collectors.toList());

            log.info("알림을 보낼 강의 수: {}", upcomingLectures.size());

            // 곧 시작할 강의가 있다면 FCM 전송
            if (!upcomingLectures.isEmpty()) {
                sendNotificationsWithoutDuplication(upcomingLectures);
            }
        } catch (Exception e) {
            log.error("예정된 강의 확인 및 알림 전송 중 오류 발생", e);
        }
    }

    /**
     * 알림 전송
     *
     * @param lectures 알림 보낼 강의 목록
     */
    private void sendNotificationsWithoutDuplication(List<LiveLectureDto> lectures) {
        Map<String, Map<String, String>> notifications = new HashMap<>();
        LocalDate today = LocalDate.now(KOREA_ZONE);
        String dayOfWeek = today.getDayOfWeek().toString().substring(0, 3);

        for (LiveLectureDto lecture : lectures) {
            String message = String.format("%s 강의가 10분 후에 시작됩니다.", lecture.getLiveTitle());

            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("body", message);
            notificationData.put("liveId", lecture.getLiveId().toString());

            Users teacher = usersRepository.findById(lecture.getUserId()).orElse(null);
            if (teacher != null && teacher.getFcmToken() != null) {
                notifications.put(teacher.getFcmToken(), notificationData);
            }

            List<MyLiveLecture> participants = myLiveLectureRepository.findParticipantsForTodayLecture(
                lecture.getLiveId(), today, dayOfWeek);

            for (MyLiveLecture participant : participants) {
                Users student = participant.getUser();
                if (student != null && student.getFcmToken() != null) {
                    notifications.put(student.getFcmToken(), notificationData);
                }
            }
        }

        // 배치로 알림 전송
        try {
            if (!notifications.isEmpty()) {
                fcmService.sendBatchMessagesWithData("강의 시작 10분 전입니다.", notifications);
                log.info("총 {} 명에게 알림 전송 완료", notifications.size());
            }
        } catch (FirebaseMessagingException e) {
            log.error("배치 알림 전송 중 오류 발생", e);
        }
    }

    /**
     * 객체 리스트를 LiveLectureDto 리스트로
     *
     * @param objects 변환할 객체 리스트
     * @return 변환된 LiveLectureDto 리스트
     */
    private List<LiveLectureDto> convertToLiveLectureDtoList(List<Object> objects) {
        return objects.stream()
            .map(obj -> {
                if (obj instanceof Map) {
                    return convertMapToLiveLectureDto((Map<String, Object>) obj);
                } else if (obj instanceof LiveLectureDto) {
                    return (LiveLectureDto) obj;
                } else {
                    throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
                }
            })
            .collect(Collectors.toList());
    }


    /**
     * Map을 LiveLectureDto로
     *
     * @param map 변환할 Map 객체
     * @return 변환된 LiveLectureDto 객체
     */
    private LiveLectureDto convertMapToLiveLectureDto(Map<String, Object> map) {
        LiveLectureDto dto = new LiveLectureDto();
        dto.setLiveId(((Number) map.get("liveId")).longValue());
        dto.setLiveTitle((String) map.get("liveTitle"));
        dto.setLiveContent((String) map.get("liveContent"));
        dto.setStartDate(Instant.parse((String) map.get("startDate")));
        dto.setEndDate(Instant.parse((String) map.get("endDate")));
        dto.setStartTime(Instant.parse((String) map.get("startTime")));
        dto.setEndTime(Instant.parse((String) map.get("endTime")));
        dto.setMaxLiveNum((Integer) map.get("maxLiveNum"));
        dto.setAvailableDay((String) map.get("availableDay"));
        dto.setRegDate(Instant.parse((String) map.get("regDate")));
        dto.setUserId((Integer) map.get("userId"));
        return dto;
    }

    /**
     * Instant에서 LocalTime 추출
     *
     * @param instant 시간 정보를 포함한 Instant
     * @return 추출된 LocalTime
     */
    private LocalTime extractTimeFromInstant(Instant instant) {
        return LocalTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Redis에서 강의 목록 불러옴
     *
     * @param redisKey Redis 키
     * @return 강의 DTO 목록
     */
    private List<LiveLectureDto> getLecturesFromRedis(String redisKey) {
        List<Object> lecturesFromRedis = (List<Object>) redisTemplate.opsForValue().get(redisKey);

        if (lecturesFromRedis == null) {
            return new ArrayList<>();
        }
        return convertToLiveLectureDtoList(lecturesFromRedis);
    }

    /**
     * 요일 문자열을 DayOfWeek 집합으로
     *
     * @param availableDays 가능한 요일 문자열
     * @return DayOfWeek 집합
     */
    private Set<DayOfWeek> parseAvailableDays(String availableDays) {
        return Arrays.stream(availableDays.split(","))
            .map(String::trim)
            .map(this::mapToDayOfWeek)
            .collect(Collectors.toSet());
    }

    /**
     * 요일 문자열을 DayOfWeek 열거형으로
     *
     * @param day 요일 문자열
     * @return 해당하는 DayOfWeek 열거형
     */
    private DayOfWeek mapToDayOfWeek(String day) {
        switch (day.toUpperCase()) {
            case "MON":
                return DayOfWeek.MONDAY;
            case "TUE":
                return DayOfWeek.TUESDAY;
            case "WED":
                return DayOfWeek.WEDNESDAY;
            case "THU":
                return DayOfWeek.THURSDAY;
            case "FRI":
                return DayOfWeek.FRIDAY;
            case "SAT":
                return DayOfWeek.SATURDAY;
            case "SUN":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    /**
     * 강의 일정 업데이트 알림 전송
     *
     * @param updatedLecture 업데이트된 강의 정보
     */
    public void sendLectureUpdateNotification(LiveLectures updatedLecture) {
        List<MyLiveLecture> participants = myLiveLectureRepository.findByLiveLectureIdWithUser(
            updatedLecture.getLiveId());

        Map<String, Map<String, String>> notifications = new HashMap<>();
        String message = String.format("%s 강의의 일정이 업데이트되었습니다.", updatedLecture.getLiveTitle());

        for (MyLiveLecture participant : participants) {
            Users user = participant.getUser();
            if (user != null && user.getFcmToken() != null) {
                Map<String, String> notificationData = new HashMap<>();
                notificationData.put("body", message);
                notificationData.put("liveId", updatedLecture.getLiveId().toString());
                notifications.put(user.getFcmToken(), notificationData);
            }
        }

        try {
            if (!notifications.isEmpty()) {
                fcmService.sendBatchMessagesWithData("강의 일정 업데이트", notifications);
                log.info("강의 ID: {}의 일정 업데이트 알림 {} 명에게 전송", updatedLecture.getLiveId(),
                    notifications.size());
            }
        } catch (FirebaseMessagingException e) {
            log.error("강의 일정 업데이트 알림 전송 중 오류 발생", e);
        }
    }

    /**
     * 강의 삭제 알림 전송
     *
     * @param myLiveLectures 삭제할 강의들 정보
     */
    public void sendLectureDeletionNotification(LiveLectures lecture,
        List<MyLiveLecture> myLiveLectures) {
        String message = String.format("강의 '%s'가 삭제되었습니다.", lecture.getLiveTitle());
        Map<String, Map<String, String>> notifications = new HashMap<>();

        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            Users user = myLiveLecture.getUser();
            if (user != null && user.getFcmToken() != null) {
                Map<String, String> notificationData = new HashMap<>();
                notificationData.put("body", message);
                notificationData.put("liveId", lecture.getLiveId().toString());
                notifications.put(user.getFcmToken(), notificationData);
            }
        }

        try {
            if (!notifications.isEmpty()) {
                fcmService.sendBatchMessagesWithData("강의 삭제 알림", notifications);
                log.info("강의 삭제 알림 {} 명에게 전송", notifications.size());
            }
        } catch (Exception e) {
            log.error("강의 삭제 알림 전송 중 오류 발생", e);
        }
    }
}