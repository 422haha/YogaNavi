package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.fcm.FCMService;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDTO;
import com.yoga.backend.members.repository.UsersRepository;

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
    private UsersRepository usersRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void handleLectureUpdate(LiveLectures updatedLecture) {
        LiveLectureDTO lectureDTO = LiveLectureDTO.fromEntity(updatedLecture);
        LocalDate startDate = lectureDTO.getStartDate().atZone(KOREA_ZONE).toLocalDate();
        LocalDate endDate = lectureDTO.getEndDate().atZone(KOREA_ZONE).toLocalDate();
        LocalTime lectureStartTime = extractTimeFromInstant(lectureDTO.getStartTime());

        Set<DayOfWeek> availableDays = parseAvailableDays(lectureDTO.getAvailableDay());

        log.info("강의 업데이트 시작 - ID: {}, 제목: {}, 시작일: {}, 종료일: {}, 요일: {}",
            lectureDTO.getLiveId(), lectureDTO.getLiveTitle(), startDate, endDate,
            lectureDTO.getAvailableDay());

        LocalDate today = LocalDate.now(KOREA_ZONE);
        boolean updatedToday = false;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (availableDays.contains(date.getDayOfWeek())) {
                String redisKey = REDIS_KEY_PREFIX + date.toString();

                List<LiveLectureDTO> lectures = getLecturesFromRedis(redisKey);

                boolean found = false;
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

                redisTemplate.opsForValue().set(redisKey, lectures);
                redisTemplate.expireAt(redisKey,
                    Date.from(date.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant()));

                log.info("강의 ID {} Redis 캐시 업데이트 완료 (날짜: {}, 시작 시간: {})",
                    lectureDTO.getLiveId(), date, lectureStartTime);

                if (date.equals(today)) {
                    updatedToday = true;
                }
            }
        }

        if (updatedToday) {
            log.info("오늘 날짜의 강의가 업데이트되어 스케줄러를 즉시 실행합니다.");
            checkUpcomingLecturesAndNotify();
        }

        log.info("강의 업데이트 완료 - ID: {}", lectureDTO.getLiveId());
    }

    private LocalTime extractTimeFromInstant(Instant instant) {
        return LocalTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private List<LiveLectureDTO> getLecturesFromRedis(String redisKey) {
        List<Object> lecturesFromRedis = (List<Object>) redisTemplate.opsForValue().get(redisKey);
        if (lecturesFromRedis == null) {
            return new ArrayList<>();
        }
        return convertToLiveLectureDTOList(lecturesFromRedis);
    }

    public void handleLectureDelete(Long liveId) {
        log.info("강의 삭제 시작 - ID: {}", liveId);
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        log.info("검색된 Redis 키 수: {}", keys.size());
        for (String key : keys) {
            List<Object> lecturesFromRedis = (List<Object>) redisTemplate.opsForValue().get(key);
            if (lecturesFromRedis != null) {
                List<LiveLectureDTO> lectures = convertToLiveLectureDTOList(lecturesFromRedis);
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

    private LiveLectureDTO convertMapToLiveLectureDTO(Map<String, Object> map) {
        LiveLectureDTO dto = new LiveLectureDTO();
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

    @Scheduled(cron = "0 0 0 * * *")
    public void cacheTodayLectures() {
        LocalDate todayKorea = LocalDate.now(KOREA_ZONE);
        String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);

        Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
        Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();

        List<LiveLectureDTO> todayLectures = liveLectureRepository.findLecturesForToday(
                startOfDayKorea, endOfDayKorea, dayAbbreviation)
            .stream()
            .map(LiveLectureDTO::fromEntity)
            .collect(Collectors.toList());

        String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
        redisTemplate.opsForValue().set(redisKey, todayLectures);
        redisTemplate.expireAt(redisKey, Date.from(endOfDayKorea));

        log.info("오늘 강의 목록 Redis 캐시 갱신 완료. 강의 개수: {}", todayLectures.size());
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void checkUpcomingLecturesAndNotify() {
        try {
            ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
            LocalDate todayKorea = nowKorea.toLocalDate();

            String redisKey = REDIS_KEY_PREFIX + todayKorea.toString();
            List<LiveLectureDTO> todayLectures = getLecturesFromRedis(redisKey);

            log.debug("오늘의 강의 수: {}", todayLectures.size());

            if (todayLectures.isEmpty()) {
                log.warn("Redis에서 오늘의 강의 목록 조회 불가. DB에서 조회.");
                String dayAbbreviation = todayKorea.getDayOfWeek().toString().substring(0, 3);
                Instant startOfDayKorea = todayKorea.atStartOfDay(KOREA_ZONE).toInstant();
                Instant endOfDayKorea = todayKorea.plusDays(1).atStartOfDay(KOREA_ZONE).toInstant();
                todayLectures = liveLectureRepository.findLecturesForToday(startOfDayKorea,
                        endOfDayKorea, dayAbbreviation)
                    .stream()
                    .map(LiveLectureDTO::fromEntity)
                    .collect(Collectors.toList());
                redisTemplate.opsForValue().set(redisKey, todayLectures);
                redisTemplate.expireAt(redisKey, Date.from(endOfDayKorea));
            }

            log.info("오늘 강의 목록 Redis 캐시 확인 완료. 강의 개수: {}", todayLectures.size());

            List<LiveLectureDTO> upcomingLectures = todayLectures.stream()
                .filter(lecture -> {

                    LocalTime lectureTime = extractTimeFromInstant(lecture.getStartTime());
                    System.out.println("lectureTime : " + lectureTime);

                    ZonedDateTime lectureDateTime = ZonedDateTime.of(todayKorea, lectureTime,
                        KOREA_ZONE);
                    System.out.println("lectureDateTime : " + lectureDateTime);

                    ZonedDateTime notificationTime = lectureDateTime.minusMinutes(10);
                    System.out.println("notificationTime : " + notificationTime);

                    log.debug("강의 ID: {}, 강의 시작 시간: {}, 알림 시간: {}, 현재 시간: {}",
                        lecture.getLiveId(), lectureDateTime, notificationTime, nowKorea);

                    return lecture.getAvailableDay()
                        .contains(todayKorea.getDayOfWeek().toString().substring(0, 3)) &&
                        nowKorea.isAfter(notificationTime.minusMinutes(10)) &&
                        nowKorea.isBefore(notificationTime);
                })
                .collect(Collectors.toList());

            log.info("알림을 보낼 강의 수: {}", upcomingLectures.size());

            for (LiveLectureDTO lecture : upcomingLectures) {
                System.out.println("lecture : " + lecture.getLiveTitle());
                sendNotificationToTeacher(lecture);
                sendNotificationToStudents(lecture);
            }
        } catch (Exception e) {
            log.error("예정된 강의 확인 및 알림 전송 중 오류 발생", e);
        }
    }

    private void sendNotificationToTeacher(LiveLectureDTO lecture) {
        Users teacher = usersRepository.findById(lecture.getUserId()).orElse(null);
        if (teacher != null && teacher.getFcmToken() != null) {
            try {
                LocalTime lectureStartTime = LocalTime.ofInstant(lecture.getStartTime(),
                    KOREA_ZONE);
                String message = String.format("%s 강의가 %s에 시작됩니다.",
                    lecture.getLiveTitle(),
                    lectureStartTime.toString());

                fcmService.sendMessage("강의 시작 10분 전입니다.", message, teacher.getEmail());
                log.info("강사 {}에게 강의 ID {} 알림 전송 성공", teacher.getEmail(), lecture.getLiveId());
            } catch (FirebaseMessagingException e) {
                log.error("강사 {}에게 강의 ID {} 알림 전송 실패", teacher.getEmail(), lecture.getLiveId(), e);
            }
        } else {
            log.warn("강사 정보 또는 FCM 토큰이 없음. 강의 ID: {}", lecture.getLiveId());
        }
    }

    private void sendNotificationToStudents(LiveLectureDTO lecture) {
        List<MyLiveLecture> enrollments = myLiveLectureRepository.findByLiveLectureWithUser(
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
                    log.info("학생 {}에게 강의 ID {} 알림 전송 성공", student.getEmail(), lecture.getLiveId());
                } catch (FirebaseMessagingException e) {
                    log.error("학생 {}에게 강의 ID {} 알림 전송 실패", student.getEmail(), lecture.getLiveId(),
                        e);
                }
            } else {
                log.warn("학생 {}의 FCM 토큰이 없음. 강의 ID: {}", student.getEmail(), lecture.getLiveId());
            }
        }
    }

    private List<LiveLectureDTO> convertToLiveLectureDTOList(List<Object> objects) {
        return objects.stream()
            .map(obj -> {
                if (obj instanceof Map) {
                    return convertMapToLiveLectureDTO((Map<String, Object>) obj);
                } else if (obj instanceof LiveLectureDTO) {
                    return (LiveLectureDTO) obj;
                } else {
                    throw new IllegalArgumentException("Unknown object type: " + obj.getClass());
                }
            })
            .collect(Collectors.toList());
    }
}