package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
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
 * todo 자정 넘어가는거 고려
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
//        System.out.println("home current date " + currentDate);

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

        // 디버깅
//        System.out.println("학생");
//        System.out.println("currentDate: " + currentDate);
//        for (HomeResponseDto historyDto : result) {
//            System.out.println("HomeResponseDto.getLiveTitle = " + historyDto.getLiveTitle());
//            System.out.println("HomeResponseDto.getLectureDay" + historyDto.getLectureDay());
//            System.out.println("HomeResponseDto.getEndTime" + historyDto.getEndTime());
//        }

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

        // 디버깅
//        System.out.println("선생");
//        System.out.println("currentDate: " + currentDate);
//        for (HomeResponseDto historyDto : result) {
//            System.out.println("HomeResponseDto.getLiveTitle = " + historyDto.getLiveTitle());
//            System.out.println("HomeResponseDto.getLectureDay" + historyDto.getLectureDay());
//            System.out.println("HomeResponseDto.getEndTime" + historyDto.getEndTime());
//        }

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

//        LocalTime startTime = LocalTime.ofInstant(lecture.getStartTime(), KOREA_ZONE);
//        LocalTime endTime = LocalTime.ofInstant(lecture.getEndTime(), KOREA_ZONE);

        LocalTime startTime = ZonedDateTime.ofInstant(lecture.getStartTime(), ZoneId.of("UTC"))
            .toLocalTime();
        LocalTime endTime = ZonedDateTime.ofInstant(lecture.getEndTime(), ZoneId.of("UTC"))
            .toLocalTime();

//        System.out.println("convert to home ============= ");
//        System.out.println("startdate: " + startDate);
//        System.out.println("enddate: " + endDate);
//        System.out.println("startTime: " + startTime);
//        System.out.println("endTime: " + endTime);

        LocalDate today = nowKorea.toLocalDate();
        LocalTime nowTime = nowKorea.toLocalTime();

//        System.out.println("nowtime: " + nowTime);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (lecture.getAvailableDay().contains(date.getDayOfWeek().toString().substring(0, 3))) {
                if (date.isAfter(today) || (date.isEqual(today) && endTime.isAfter(nowTime))) {
                    HomeResponseDto dto = createHomeResponseDto(lecture, date, startTime,
                        endTime, isTeacher);
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
        LocalTime startTime, LocalTime endTime, boolean isTeacher) {
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