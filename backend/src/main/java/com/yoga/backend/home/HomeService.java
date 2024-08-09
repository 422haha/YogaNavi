package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.livelectures.LiveLectureRepository;
import com.yoga.backend.livelectures.MyLiveLectureRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 내가 듣거나 진행할 강의들
 */
@Service
public class HomeService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final LiveLectureRepository liveLectureRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final UsersRepository usersRepository;

    public HomeService(
        LiveLectureRepository liveLectureRepository,
        MyLiveLectureRepository myLiveLectureRepository,
        UsersRepository usersRepository) {
        this.liveLectureRepository = liveLectureRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * 사용자의 진행/수강할 강의 조회
     *
     * @param userId 사용자 ID
     * @return 강의 이력 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<HomeResponseDto> getHomeData(int userId) {
        ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
        String dayOfWeek = nowKorea.getDayOfWeek().toString().substring(0, 3);

        List<HomeResponseDto> result = new ArrayList<>();

        result.addAll(getPastUserLectures(userId, nowKorea, dayOfWeek));
        result.addAll(getPastStudentLectures(userId, nowKorea, dayOfWeek));

        return sortHomeData(result);
    }

    /**
     * 학생의 수강할 강의들
     */
    @Transactional(readOnly = true)
    protected List<HomeResponseDto> getPastStudentLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        LocalDate currentDate = nowKorea.toLocalDate();

        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findCurrentLecturesByUserId(
            userId, currentDate, dayOfWeek);

        List<HomeResponseDto> result = new ArrayList<>();

        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            LiveLectures lecture = myLiveLecture.getLiveLecture();
            List<HomeResponseDto> dtos = convertToHomeResponseDto(lecture, myLiveLecture,
                nowKorea, false);

            for (HomeResponseDto dto : dtos) {
                Users teacher = lecture.getUser();

                dto.setProfileImageUrl(teacher.getProfile_image_url());
                dto.setProfileImageUrlSmall(teacher.getProfile_image_url_small());
                result.add(dto);
            }
        }

        return result;
    }

    /**
     * 강사의 강의 이력
     */
    @Transactional(readOnly = true)
    protected List<HomeResponseDto> getPastUserLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        LocalDate currentDate = nowKorea.toLocalDate();

        List<LiveLectures> lectures = liveLectureRepository.findLecturesByUserAndDateRange(userId,
            currentDate, dayOfWeek);

        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<HomeResponseDto> result = new ArrayList<>();

        for (LiveLectures lecture : lectures) {

            List<HomeResponseDto> dtos = convertToHomeResponseDto(lecture, null, nowKorea,
                true);

            for (HomeResponseDto dto : dtos) {
                // 강사의 프로필 이미지 URL 설정
                dto.setProfileImageUrl(user.getProfile_image_url());
                dto.setProfileImageUrlSmall(user.getProfile_image_url_small());
                result.add(dto);
            }
        }

        return result;
    }

    /**
     * LiveLectures Entity -> HomeResponseDto
     */
    private List<HomeResponseDto> convertToHomeResponseDto(LiveLectures lecture,
        MyLiveLecture myLiveLecture, ZonedDateTime nowKorea, boolean isTeacher) {
        List<HomeResponseDto> dtos = new ArrayList<>();

        LocalDate startDate;
        LocalDate endDate;
        if (myLiveLecture != null) {
            startDate = myLiveLecture.getStartDate().atZone(ZoneOffset.UTC)
                .withZoneSameInstant(KOREA_ZONE).toLocalDate();
            endDate = myLiveLecture.getEndDate().atZone(ZoneOffset.UTC)
                .withZoneSameInstant(KOREA_ZONE).toLocalDate();
        } else {
            startDate = lecture.getStartDate().atZone(ZoneOffset.UTC)
                .withZoneSameInstant(KOREA_ZONE).toLocalDate();
            endDate = lecture.getEndDate().atZone(ZoneOffset.UTC)
                .withZoneSameInstant(KOREA_ZONE).toLocalDate();
        }

        LocalTime startTime = ZonedDateTime.ofInstant(lecture.getStartTime(), ZoneId.of("UTC"))
            .toLocalTime();
        LocalTime endTime = ZonedDateTime.ofInstant(lecture.getEndTime(), ZoneId.of("UTC"))
            .toLocalTime();

        LocalDate today = nowKorea.toLocalDate();
        LocalTime nowTime = nowKorea.toLocalTime();

        boolean overNight = endTime.isBefore(startTime);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (lecture.getAvailableDay()
                .contains(date.getDayOfWeek().toString().substring(0, 3))) {

                // 오늘 날짜 강의 && 종료 시간이 현재 시간 이후 || 자정을 넘음
                boolean isLectureToday =
                    date.isEqual(today) && (endTime.isAfter(nowTime) || overNight);

                // 어제 시작된 강의가 오늘까지 이어짐 (자정을 넘고, 현재 시간이 종료 시간 이전)
                boolean isLectureStartedYesterday =
                    date.equals(today.minusDays(1)) && overNight && endTime.isAfter(nowTime);

                // 미래 강의
                boolean isFutureLecture = date.isAfter(today);

                if (isFutureLecture || isLectureToday || isLectureStartedYesterday) {

                    boolean isOnAir = false;
                    if (isLectureStartedYesterday) { // 어제 시작된 강의가 오늘까지 이어짐
                        isOnAir = lecture.getIsOnAir();
                    } else if (isLectureToday) { // 오늘 날짜 강의
                        isOnAir = lecture.getIsOnAir();
                    }

                    HomeResponseDto dto = createHomeResponseDto(lecture, date, startTime, endTime,
                        isTeacher, isOnAir);
                    dtos.add(dto);
                }
            }
        }

        return dtos;
    }

    /**
     * HomeResponseDto 생성
     */
    private HomeResponseDto createHomeResponseDto(LiveLectures lecture, LocalDate date,
        LocalTime startTime, LocalTime endTime, boolean isTeacher, boolean isOnAir) {
        HomeResponseDto dto = new HomeResponseDto();

        dto.setLiveId(lecture.getLiveId());
        dto.setUserId(lecture.getUser().getId());
        dto.setNickname(lecture.getUser().getNickname());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());

        ZonedDateTime lectureDateTime = date.atStartOfDay(KOREA_ZONE);
        ZonedDateTime gmtLectureDateTime = lectureDateTime.withZoneSameInstant(ZoneOffset.UTC);
        dto.setLectureDate(gmtLectureDateTime.toInstant().toEpochMilli());

        ZonedDateTime startDateTime = date.atTime(startTime).atZone(ZoneId.of("UTC"));
        ZonedDateTime endDateTime = date.atTime(endTime).atZone(ZoneId.of("UTC"));
        dto.setStartTime(
            startDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalTime().toNanoOfDay()
                / 1_000_000);
        dto.setEndTime(endDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalTime().toNanoOfDay()
            / 1_000_000);

        dto.setRegDate(
            lecture.getRegDate().atZone(ZoneOffset.UTC).withZoneSameInstant(KOREA_ZONE).toInstant()
                .toEpochMilli());
        dto.setLectureDay(date.getDayOfWeek().toString().substring(0, 3));
        dto.setMaxLiveNum(lecture.getMaxLiveNum());

        dto.setProfileImageUrl(lecture.getUser().getProfile_image_url());
        dto.setProfileImageUrlSmall(lecture.getUser().getProfile_image_url_small());

        dto.setTeacher(isTeacher);
        dto.setIsOnAir(lecture.getIsOnAir());

        dto.setIsOnAir(isOnAir);

        return dto;
    }

    /**
     * 오름차순 정렬
     */
    private List<HomeResponseDto> sortHomeData(List<HomeResponseDto> data) {
        return data.stream()
            .sorted(Comparator
                .comparingLong(HomeResponseDto::getLectureDate)
                .thenComparingLong(HomeResponseDto::getStartTime))
            .collect(Collectors.toList());
    }
}