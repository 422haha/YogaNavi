package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.fcm.FCMService;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessagingException;

@Slf4j
@Service
public class NotificationService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final String REDIS_KEY_PREFIX = "yoga:lectures:";

    @Autowired
    private LiveLectureRepository liveLectureRepository;

    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void handleLectureUpdate(LiveLectures updatedLecture) {
        LocalDate startDate = updatedLecture.getStartDate().atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalDate endDate = updatedLecture.getEndDate().atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalTime lectureStartTime = updatedLecture.getStartTime().atZone(ZoneId.systemDefault())
            .toLocalTime();

        // 가능한 요일 문자열을 DayOfWeek 집합으로
        Set<DayOfWeek> availableDays = parseAvailableDays(updatedLecture.getAvailableDay());

        log.info("강의 업데이트 시작 - ID: {}, 제목: {}, 시작일: {}, 종료일: {}, 요일: {}",
            updatedLecture.getLiveId(), updatedLecture.getLiveTitle(), startDate, endDate,
            updatedLecture.getAvailableDay());

        // 시작일부터 종료일까지 각 날짜에 대해
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 해당 날짜가 강의 가능 요일인 경우에만
            if (availableDays.contains(date.getDayOfWeek())) {
                String redisKey = REDIS_KEY_PREFIX + date.toString();

                // redis에서 해당 날짜의 강의 목록 조회
                List<LiveLectures> lectures = (List<LiveLectures>) redisTemplate.opsForValue()
                    .get(redisKey);

                // 강의 목록이 없으면 새로운 리스트 생성
                if (lectures == null) {
                    lectures = new ArrayList<>();
                    log.info("새 강의 목록 생성 - 날짜: {}", date);
                }

                // 기존 강의 업데이트 or 새 강의 추가
                boolean found = false;
                for (int i = 0; i < lectures.size(); i++) {
                    if (lectures.get(i).getLiveId().equals(updatedLecture.getLiveId())) {
                        lectures.set(i, updatedLecture);
                        found = true;
                        log.info("기존 강의 업데이트 - 날짜: {}, 강의 ID: {}", date,
                            updatedLecture.getLiveId());
                        break;
                    }
                }

                // 기존 강의가 없으면 새로 추가
                if (!found) {
                    lectures.add(updatedLecture);
                    log.info("새 강의 추가 - 날짜: {}, 강의 ID: {}", date, updatedLecture.getLiveId());
                }

                // 업데이트된 강의 목록 redis에 저장
                redisTemplate.opsForValue().set(redisKey, lectures);

                // redis 캐시의 만료 시간을 다음날 자정으로
                redisTemplate.expireAt(redisKey,
                    Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

                log.info("강의 ID {} Redis 캐시 업데이트 완료 (날짜: {}, 시작 시간: {})",
                    updatedLecture.getLiveId(), date, lectureStartTime);
            }
        }
        log.info("강의 업데이트 완료 - ID: {}", updatedLecture.getLiveId());
    }

    public void handleLectureDelete(Long liveId) {
        log.info("강의 삭제 시작 - ID: {}", liveId);
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        log.info("검색된 Redis 키 수: {}", keys.size());
        for (String key : keys) {
            List<LiveLectures> lectures = (List<LiveLectures>) redisTemplate.opsForValue().get(key);
            if (lectures != null) {
                boolean removed = lectures.removeIf(lecture -> lecture.getLiveId().equals(liveId));
                if (removed) {
                    redisTemplate.opsForValue().set(key, lectures);
                    log.info("강의 ID {} Redis 캐시에서 삭제 완료 (키: {})", liveId, key);
                }
            }
        }
    }

    private Set<DayOfWeek> parseAvailableDays(String availableDays) {
        return Arrays.stream(availableDays.split(","))
            .map(String::trim)
            .map(this::mapToDayOfWeek)
            .collect(Collectors.toSet());
    }

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

    // 매일 자정에 오늘 강의 목록 캐시
    @Scheduled(cron = "0 0 0 * * *")
    public void cacheTodayLectures() {
        Instant nowUtc = Instant.now();
        LocalDate todayKorea = LocalDate.ofInstant(nowUtc, KOREA_ZONE);
        String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);

        Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
        Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();

        List<LiveLectures> todayLectures = liveLectureRepository.findLecturesForToday(
            startOfDayKorea, endOfDayKorea, dayAbbreviation);

        String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
        redisTemplate.opsForValue().set(redisKey, todayLectures); // 오늘 강의 목록 Redis에 저장
        redisTemplate.expireAt(redisKey,
            Date.from(endOfDayKorea)); // redis에 저장된 데이터 만료 시간을 다음날 자정으로

        log.info("오늘 강의 목록 Redis 캐시 갱신 완료. 강의 개수: {}", todayLectures.size());
    }

    // 매 10분마다 실행, 곧 시작할 강의의 알림을 처리
    @Scheduled(cron = "0 */10 * * * *")
    public void checkUpcomingLecturesAndNotify() {
        try {
            ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
            LocalDate todayKorea = nowKorea.toLocalDate();
            LocalTime nowTimeKorea = nowKorea.toLocalTime();

            String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
            List<LiveLectures> todayLectures = (List<LiveLectures>) redisTemplate.opsForValue()
                .get(redisKey); // redis에서 오늘의 강의 목록 조회

            if (todayLectures == null) {
                log.warn("Redis에서 오늘의 강의 목록 조회 불가. DB에서 조회.");
                String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);
                Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
                Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();
                todayLectures = liveLectureRepository.findLecturesForToday(startOfDayKorea,
                    endOfDayKorea, dayAbbreviation);// DB에서 오늘의 강의 목록 조회
                redisTemplate.opsForValue().set(redisKey, todayLectures); // 조회한 데이터 redis에 다시 저장
                redisTemplate.expireAt(redisKey,
                    Date.from(endOfDayKorea)); // redis에 저장된 데이터 만료 시간 다음날 자정으로
            }

            // 현재 시간 기준으로 10분 이내에 시작하는 강의 필터링
            List<LiveLectures> upcomingLectures = todayLectures.stream()
                .filter(lecture -> {
                    LocalDate lectureDate = LocalDate.ofInstant(lecture.getStartDate(), KOREA_ZONE);
                    LocalTime lectureTime = LocalTime.ofInstant(lecture.getStartTime(), KOREA_ZONE);
                    LocalDateTime lectureDateTime = LocalDateTime.of(lectureDate, lectureTime);
                    LocalDateTime notificationTime = lectureDateTime.minusMinutes(10);
                    return lectureDate.equals(todayKorea) &&
                        lecture.getAvailableDay()
                            .contains(todayKorea.getDayOfWeek().toString().substring(0, 3)) &&
                        nowTimeKorea.isAfter(notificationTime.toLocalTime().minusMinutes(10)) &&
                        nowTimeKorea.isBefore(notificationTime.toLocalTime());
                })
                .collect(Collectors.toList());

            for (LiveLectures lecture : upcomingLectures) {
                sendNotificationToTeacher(lecture);
                sendNotificationToStudents(lecture);
            }
        } catch (Exception e) {
            log.error("예정된 강의 확인 및 알림 전송 중 오류 발생", e);
        }
    }

    private void sendNotificationToTeacher(LiveLectures lecture) {
        Users teacher = lecture.getUser();
        if (teacher.getFcmToken() != null) {
            try {
                LocalTime lectureStartTime = LocalTime.ofInstant(lecture.getStartTime(),
                    KOREA_ZONE);
                String message = String.format("%s 강의가 %s에 시작됩니다.",
                    lecture.getLiveTitle(),
                    lectureStartTime.toString());

                fcmService.sendMessage("강의 시작 10분 전입니다.", message, teacher.getEmail());
                log.info("강사 {}에게 강의 ID {} 알림 전송 성공", teacher.getEmail(), lecture.getLiveId());
            } catch (FirebaseMessagingException e) {
                log.error("강사 {}에게 강의 ID {} 알림 전송 실패", teacher.getEmail(), lecture.getLiveId(),
                    e);
            }
        } else {
            log.warn("강사 {}의 FCM 토큰이 없음. 강의 ID: {}", teacher.getEmail(), lecture.getLiveId());
        }
    }

    private void sendNotificationToStudents(LiveLectures lecture) {
        List<MyLiveLecture> enrollments = myLiveLectureRepository.findByLiveLecture_LiveId(
            lecture.getLiveId());
        for (MyLiveLecture enrollment : enrollments) {
            Users student = enrollment.getUser();
            if (student.getFcmToken() != null) {
                try {
                    LocalTime lectureStartTime = LocalTime.ofInstant(lecture.getStartTime(),
                        KOREA_ZONE);
                    String message = String.format("%s 강의가 %s에 시작됩니다.",
                        lecture.getLiveTitle(),
                        lectureStartTime.toString());

                    fcmService.sendMessage("강의 시작 10분 전입니다.", message, student.getEmail());
                    log.info("학생 {}에게 강의 ID {} 알림 전송 성공", student.getEmail(),
                        lecture.getLiveId());
                } catch (FirebaseMessagingException e) {
                    log.error("학생 {}에게 강의 ID {} 알림 전송 실패", student.getEmail(),
                        lecture.getLiveId(), e);
                }
            } else {
                log.warn("학생 {}의 FCM 토큰이 없음. 강의 ID: {}", student.getEmail(),
                    lecture.getLiveId());
            }
        }
    }
}