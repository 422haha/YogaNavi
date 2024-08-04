package com.yoga.backend.home;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 홈 화면에 표시될 데이터 관리
 */
@Slf4j
@Service
public class HomeService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final String REDIS_KEY_PREFIX = "yoga:home:lectures:";

    private final MyLiveLectureRepository myLiveLectureRepository;
    private final LiveLectureRepository liveLectureRepository;
    private final UsersRepository usersRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, Object> redisTemplate;

    public HomeService(MyLiveLectureRepository myLiveLectureRepository,
        LiveLectureRepository liveLectureRepository,
        UsersRepository usersRepository,
        S3Service s3Service,
        RedisTemplate<String, Object> redisTemplate) {
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.liveLectureRepository = liveLectureRepository;
        this.usersRepository = usersRepository;
        this.s3Service = s3Service;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 사용자의 홈 화면 데이터 조회. 캐시된 데이터가 있으면 사용, 없으면 db에서 캐시
     *
     * @param userId 사용자 ID
     * @return 홈 화면에 표시될 데이터 목록
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<HomeResponseDto> getHomeData(int userId) {
        ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
        String redisKey = REDIS_KEY_PREFIX + nowKorea.toLocalDate().toString() + ":" + userId;

        log.info("사용자 ID {}의 홈 데이터 조회 시작", userId);

        // redis로부터 데이터 가져옴
        List<HomeResponseDto> cachedData = getCachedHomeData(redisKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            log.info("캐시된 데이터 사용: 사용자 ID {}", userId);
            return filterAndSortHomeData(cachedData, nowKorea);
        }

        // 캐시에 데이터가 없는 경우 db를 조회
        log.info("DB에서 데이터 조회: 사용자 ID {}", userId);
        List<HomeResponseDto> result = new ArrayList<>();
        result.addAll(getTeacherLectures(userId, nowKorea));
        result.addAll(getStudentLectures(userId, nowKorea));

        // 결과를 캐시
        cacheHomeData(redisKey, result);

        return filterAndSortHomeData(result, nowKorea);
    }

    // 강사 자신의 강의 조회
    private List<HomeResponseDto> getTeacherLectures(int userId, ZonedDateTime nowKorea) {
        return liveLectureRepository.findByUserIdAndStartDateBetween(
                userId,
                nowKorea.toInstant(),
                nowKorea.plusDays(1).toInstant()
            ).stream()
            .map(lecture -> convertToHomeResponseDto(lecture, true))
            .collect(Collectors.toList());
    }

    // 학생의 수강 강의 조회
    private List<HomeResponseDto> getStudentLectures(int userId, ZonedDateTime nowKorea) {
        return myLiveLectureRepository.findByUserIdAndStartDateBetween(
                userId,
                nowKorea.toInstant(),
                nowKorea.plusDays(1).toInstant()
            ).stream()
            .map(myLecture -> convertToHomeResponseDto(myLecture.getLiveLecture(), false))
            .collect(Collectors.toList());
    }

    // DTO 변환
    private HomeResponseDto convertToHomeResponseDto(LiveLectures lecture, boolean isTeacher) {
        HomeResponseDto dto = new HomeResponseDto();
        dto.setLiveId(lecture.getLiveId());
        dto.setUserId(lecture.getUser().getId());
        dto.setNickname(lecture.getUser().getNickname());
        dto.setProfileImageUrl(lecture.getUser().getProfile_image_url());
        dto.setProfileImageUrlSmall(lecture.getUser().getProfile_image_url_small());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());

        LocalDate lectureDate = lecture.getStartDate().atZone(KOREA_ZONE).toLocalDate();
        LocalTime lectureStartTime = lecture.getStartTime().atZone(KOREA_ZONE).toLocalTime();
        LocalTime lectureEndTime = lecture.getEndTime().atZone(KOREA_ZONE).toLocalTime();

        dto.setStartTime(
            lectureDate.atTime(lectureStartTime).atZone(KOREA_ZONE).toInstant().toEpochMilli());
        dto.setEndTime(
            lectureDate.atTime(lectureEndTime).atZone(KOREA_ZONE).toInstant().toEpochMilli());
        dto.setLectureDate(lectureDate.atStartOfDay(KOREA_ZONE).toInstant().toEpochMilli());
        dto.setRegDate(lecture.getRegDate().toEpochMilli());
        dto.setLectureDay(lecture.getAvailableDay());
        dto.setMaxLiveNum(lecture.getMaxLiveNum());
        dto.setTeacher(isTeacher);
        return dto;
    }

    private List<HomeResponseDto> filterAndSortHomeData(List<HomeResponseDto> data,
        ZonedDateTime nowKorea) {
        long nowMillis = nowKorea.toInstant().toEpochMilli();
        return data.stream()
            .filter(dto -> dto.getStartTime() > nowMillis)
            .sorted(Comparator.comparingLong(HomeResponseDto::getStartTime))
            .collect(Collectors.toList());
    }

    private List<HomeResponseDto> getCachedHomeData(String redisKey) {
        try {
            List<Object> dataFromRedis = (List<Object>) redisTemplate.opsForValue().get(redisKey);

            if (dataFromRedis == null) {
                return new ArrayList<>();
            }

            log.debug("redis에서 가져온 데이터: {}", dataFromRedis);

            return convertToHomeResponseDtoList(dataFromRedis);
        } catch (Exception e) {
            log.error("redis에서 홈 데이터를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<HomeResponseDto> convertToHomeResponseDtoList(List<Object> objects) {
        return objects.stream()
            .map(obj -> {
                try {
                    if (obj instanceof Map) {
                        return convertMapToHomeResponseDto((Map<String, Object>) obj);
                    } else if (obj instanceof HomeResponseDto) {
                        return (HomeResponseDto) obj;
                    } else {
                        log.warn("알 수 없는 객체 타입: {}", obj.getClass());
                        return null;
                    }
                } catch (Exception e) {
                    log.error("객체 변환 중 오류 발생: {}", e.getMessage(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private HomeResponseDto convertMapToHomeResponseDto(Map<String, Object> map) {
        HomeResponseDto dto = new HomeResponseDto();
        dto.setLiveId(((Number) map.get("liveId")).longValue());
        dto.setUserId((Integer) map.get("userId"));
        dto.setNickname((String) map.get("nickname"));
        dto.setProfileImageUrl((String) map.get("profileImageUrl"));
        dto.setProfileImageUrlSmall((String) map.get("profileImageUrlSmall"));
        dto.setLiveTitle((String) map.get("liveTitle"));
        dto.setLiveContent((String) map.get("liveContent"));
        dto.setStartTime(((Number) map.get("startTime")).longValue());
        dto.setEndTime(((Number) map.get("endTime")).longValue());
        dto.setLectureDate(((Number) map.get("lectureDate")).longValue());
        dto.setRegDate(((Number) map.get("regDate")).longValue());
        dto.setLectureDay((String) map.get("lectureDay"));
        dto.setMaxLiveNum((Integer) map.get("maxLiveNum"));
        dto.setTeacher((Boolean) map.get("teacher"));
        return dto;
    }

    private void cacheHomeData(String redisKey, List<HomeResponseDto> data) {
        try {
            redisTemplate.opsForValue().set(redisKey, data);
            redisTemplate.expireAt(redisKey,
                Date.from(ZonedDateTime.now(KOREA_ZONE).plusDays(1).toInstant()));
            log.info("홈 데이터 캐시 저장 완료: 키 {}, 데이터 크기: {}", redisKey, data.size());
        } catch (Exception e) {
            log.error("홈 데이터 캐시 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 매일 23시 55분에 다음날 강의 목록 캐시에 저장
     */
    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void cacheTomorrowLectures() {
        ZonedDateTime tomorrow = ZonedDateTime.now(KOREA_ZONE).plusDays(1).withHour(0).withMinute(0)
            .withSecond(0);
        log.info("내일({})의 강의 목록 캐싱 시작", tomorrow.toLocalDate());
        List<Users> allUsers = StreamSupport.stream(usersRepository.findAll().spliterator(), false)
            .filter(user -> !user.getIsDeleted())
            .collect(Collectors.toList());

        for (Users user : allUsers) {
            String redisKey =
                REDIS_KEY_PREFIX + tomorrow.toLocalDate().toString() + ":" + user.getId();
            List<HomeResponseDto> tomorrowLectures = new ArrayList<>();
            tomorrowLectures.addAll(getTeacherLectures(user.getId(), tomorrow));
            tomorrowLectures.addAll(getStudentLectures(user.getId(), tomorrow));
            cacheHomeData(redisKey, tomorrowLectures);
            log.info("사용자 ID {}의 내일 강의 목록 캐싱 완료", user.getId());
        }
    }

    /**
     * 매일 자정에 전날 캐시된 강의 목록을 삭제
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void cleanExpiredCache() {
        ZonedDateTime yesterday = ZonedDateTime.now(KOREA_ZONE).minusDays(1);
        String pattern = REDIS_KEY_PREFIX + yesterday.toLocalDate().toString() + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("어제({})의 캐시된 강의 목록 삭제 완료", yesterday.toLocalDate());
        }
    }

    /**
     * 강의 생성 시 캐시 업데이트
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void handleLectureCreation(LiveLectures newLecture) {
        updateCacheForLecture(newLecture, true);
    }

    /**
     * 강의 수정 시 캐시 업데이트
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void handleLectureUpdate(LiveLectures updatedLecture) {
        updateCacheForLecture(updatedLecture, false);
    }

    /**
     * 강의 삭제 시 캐시 업데이트
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void handleLectureDeletion(Long lectureId) {
        String pattern = REDIS_KEY_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null) {
            for (String key : keys) {
                List<HomeResponseDto> cachedData = getCachedHomeData(key);
                if (cachedData != null) {
                    cachedData.removeIf(dto -> dto.getLiveId().equals(lectureId));
                    cacheHomeData(key, cachedData);
                }
            }
        }
        log.info("강의 ID {}의 삭제 처리 완료", lectureId);
    }

    private void updateCacheForLecture(LiveLectures lecture, boolean isNewLecture) {
        ZonedDateTime lectureStart = ZonedDateTime.ofInstant(lecture.getStartDate(), KOREA_ZONE);
        ZonedDateTime lectureEnd = ZonedDateTime.ofInstant(lecture.getEndDate(), KOREA_ZONE);

        for (ZonedDateTime date = lectureStart; !date.isAfter(lectureEnd);
            date = date.plusDays(1)) {
            if (isLectureDayValid(lecture.getAvailableDay(), date)) {
                String redisKey =
                    REDIS_KEY_PREFIX + date.toLocalDate().toString() + ":" + lecture.getUser()
                        .getId();
                List<HomeResponseDto> cachedData = getCachedHomeData(redisKey);
                if (cachedData == null) {
                    cachedData = new ArrayList<>();
                }

                if (isNewLecture) {
                    cachedData.add(convertToHomeResponseDto(lecture, true));
                } else {
                    cachedData.removeIf(dto -> dto.getLiveId().equals(lecture.getLiveId()));
                    cachedData.add(convertToHomeResponseDto(lecture, true));
                }

                cacheHomeData(redisKey, cachedData);

                // 학생들 캐시 업데이트
                List<MyLiveLecture> enrolledStudents = myLiveLectureRepository.findByLiveLecture_LiveId(
                    lecture.getLiveId());
                for (MyLiveLecture enrollment : enrolledStudents) {
                    String studentRedisKey = REDIS_KEY_PREFIX + date.toLocalDate().toString() + ":"
                        + enrollment.getUser().getId();
                    List<HomeResponseDto> studentCachedData = getCachedHomeData(studentRedisKey);
                    if (studentCachedData == null) {
                        studentCachedData = new ArrayList<>();
                    }

                    studentCachedData.removeIf(dto -> dto.getLiveId().equals(lecture.getLiveId()));
                    studentCachedData.add(convertToHomeResponseDto(lecture, false));
                    cacheHomeData(studentRedisKey, studentCachedData);
                }
            }
        }
    }

    private boolean isLectureDayValid(String availableDays, ZonedDateTime date) {
        DayOfWeek dayOfWeek = mapToDayOfWeek(
            date.getDayOfWeek().toString().substring(0, 3).toUpperCase());
        return availableDays.contains(dayOfWeek.toString().substring(0, 3));
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
}