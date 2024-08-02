package com.yoga.backend.fcm;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * fcm 알림 전송 서비스
 */
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

    /**
     * 강의 업데이트시 redis에 업데이트된 강의 저장
     *
     * @param updatedLecture 업데이트된 강의 정보
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
     * 매일 자정에 실행, 오늘 할 강의 캐시
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cacheTodayLectures() {
        LocalDate todayKorea = LocalDate.now(KOREA_ZONE);
        String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);
        Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
        Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();

        // 오늘 강의 목록
        List<LiveLectureDto> todayLectures = liveLectureRepository.findLecturesForToday(
                startOfDayKorea, endOfDayKorea, dayAbbreviation)
            .stream()
            .map(LiveLectureDto::fromEntity)
            .collect(Collectors.toList());

        // redis에 오늘의 강의 목록 저장
        String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
        redisTemplate.opsForValue().set(redisKey, todayLectures);
        redisTemplate.expireAt(redisKey, Date.from(endOfDayKorea));
        log.info("오늘 강의 목록 Redis 캐시 갱신 완료. 강의 개수: {}", todayLectures.size());
    }

    /**
     * 1분마다 실행, 예정된 강의를 확인 및 알림 전송
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void checkUpcomingLecturesAndNotify() {
        try {
            ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
            LocalDate todayKorea = nowKorea.toLocalDate();
            String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
            List<LiveLectureDto> todayLectures = getLecturesFromRedis(redisKey);
            log.debug("오늘의 강의 수: {}", todayLectures.size());

            // 캐시가 비어있으면
            if (todayLectures.isEmpty()) {
                log.warn("Redis에서 오늘의 강의 목록 조회 불가. DB에서 조회.");
                String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);
                Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
                Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();

                todayLectures = liveLectureRepository.findLecturesForToday(startOfDayKorea,
                        endOfDayKorea, dayAbbreviation)
                    .stream()
                    .map(LiveLectureDto::fromEntity)
                    .collect(Collectors.toList());

                redisTemplate.opsForValue().set(redisKey, todayLectures);
                redisTemplate.expireAt(redisKey, Date.from(endOfDayKorea));
            }
            log.info("오늘 강의 목록 Redis 캐시 확인 완료. 강의 개수: {}", todayLectures.size());

            // 10분 후에 시작하는 강의 찾기
            List<LiveLectureDto> upcomingLectures = todayLectures.stream()
                .filter(lecture -> {
                    LocalTime lectureTime = extractTimeFromInstant(lecture.getStartTime());
                    ZonedDateTime lectureDateTime = ZonedDateTime.of(todayKorea, lectureTime,
                        KOREA_ZONE);
                    ZonedDateTime notificationTime = lectureDateTime.minusMinutes(10);

                    log.debug("강의 ID: {}, 강의 시작 시간: {}, 알림 시간: {}, 현재 시간: {}",
                        lecture.getLiveId(), lectureDateTime, notificationTime, nowKorea);

                    return lecture.getAvailableDay()
                        .contains(todayKorea.getDayOfWeek().toString().substring(0, 3)) &&
                        nowKorea.isAfter(notificationTime.minusSeconds(30)) &&
                        nowKorea.isBefore(notificationTime.plusSeconds(30));
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

        for (LiveLectureDto lecture : lectures) {
            String message = String.format("%s 강의가 10분 후에 시작됩니다.", lecture.getLiveTitle());

            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("body", message);
            notificationData.put("liveId", lecture.getLiveId().toString());

            // 강사, 학생 알림 준비
            List<MyLiveLecture> participants = myLiveLectureRepository.findByLiveLectureIdWithUser(
                lecture.getLiveId());
            for (MyLiveLecture participant : participants) {
                Users user = participant.getUser();
                if (user != null && user.getFcmToken() != null) {
                    notifications.put(user.getFcmToken(), notificationData);
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void sendLectureUpdateNotification(LiveLectures updatedLecture) {
        List<MyLiveLecture> participants = myLiveLectureRepository.findByLiveLectureIdWithUser(updatedLecture.getLiveId());

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
                log.info("강의 ID: {}의 일정 업데이트 알림 {} 명에게 전송", updatedLecture.getLiveId(), notifications.size());
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