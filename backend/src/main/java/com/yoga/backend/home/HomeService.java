package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.service.S3Service;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* todo 수강신청 시 수강신청 끝 날짜가 아니라 강의 끝날짜가 나옴
* */

@Slf4j
@Service
public class HomeService {
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final long PRESIGNED_URL_EXPIRATION = 3600;
    private final LiveLectureRepository liveLectureRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final UsersRepository usersRepository;
    private final S3Service s3Service;
    public HomeService(
        LiveLectureRepository liveLectureRepository,
        MyLiveLectureRepository myLiveLectureRepository,
        UsersRepository usersRepository,
        S3Service s3Service) {
        this.liveLectureRepository = liveLectureRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.usersRepository = usersRepository;
        this.s3Service = s3Service;
    }
    @Transactional(readOnly = true)
    public List<HomeResponseDto> getHomeData(int userId) {
        ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
        String dayOfWeek = nowKorea.getDayOfWeek().toString().substring(0, 3);
        List<HomeResponseDto> result = new ArrayList<>();
        result.addAll(getUserLectures(userId, nowKorea, dayOfWeek));
        result.addAll(getStudentLectures(userId, nowKorea, dayOfWeek));
        return sortHomeData(result);
    }

    @Transactional(readOnly = true)
    protected List<HomeResponseDto> getStudentLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        Instant currentDate = nowKorea.toInstant();
        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findCurrentLecturesByUserId(
            userId, currentDate, dayOfWeek);

        List<HomeResponseDto> result = new ArrayList<>();
        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            LiveLectures lecture = myLiveLecture.getLiveLecture();
            List<HomeResponseDto> dtos = convertToHomeResponseDto(lecture, nowKorea, false);
            for (HomeResponseDto dto : dtos) {
                Users teacher = lecture.getUser();
                dto.setProfileImageUrl(generatePresignedUrl(teacher.getProfile_image_url()));
                dto.setProfileImageUrlSmall(generatePresignedUrl(teacher.getProfile_image_url_small()));
                result.add(dto);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    protected List<HomeResponseDto> getUserLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        Instant currentDate = nowKorea.toInstant();
        Instant endDate = nowKorea.plusMonths(1).toInstant();
        List<LiveLectures> lectures = liveLectureRepository.findLecturesByUserAndDateRange(
            userId, currentDate, endDate, dayOfWeek);
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<HomeResponseDto> result = new ArrayList<>();
        for (LiveLectures lecture : lectures) {
            List<HomeResponseDto> dtos = convertToHomeResponseDto(lecture, nowKorea, true);
            for (HomeResponseDto dto : dtos) {
                dto.setProfileImageUrl(generatePresignedUrl(user.getProfile_image_url()));
                dto.setProfileImageUrlSmall(generatePresignedUrl(user.getProfile_image_url_small()));
                result.add(dto);
            }
        }
        return result;
    }

    private String generatePresignedUrl(String url) {
        if (url != null && !url.isEmpty()) {
            return s3Service.generatePresignedUrl(url, PRESIGNED_URL_EXPIRATION);
        }
        return null;
    }
    private List<HomeResponseDto> convertToHomeResponseDto(LiveLectures lecture,
        ZonedDateTime nowKorea, boolean isTeacher) {
        List<HomeResponseDto> dtos = new ArrayList<>();
        LocalDate startDate = lecture.getStartDate().atZone(ZoneOffset.UTC)
            .withZoneSameInstant(KOREA_ZONE).toLocalDate();
        LocalDate endDate = lecture.getEndDate().atZone(ZoneOffset.UTC)
            .withZoneSameInstant(KOREA_ZONE).toLocalDate();
        LocalTime startTime = LocalTime.ofInstant(lecture.getStartTime(), KOREA_ZONE);
        LocalTime endTime = LocalTime.ofInstant(lecture.getEndTime(), KOREA_ZONE);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (lecture.getAvailableDay()
                .contains(date.getDayOfWeek().toString().substring(0, 3))) {
                ZonedDateTime lectureDateTime = date.atTime(startTime).atZone(KOREA_ZONE);
                if (lectureDateTime.isAfter(nowKorea)) {
                    HomeResponseDto dto = createHomeResponseDto(lecture, date, startTime, endTime,
                        isTeacher);
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }
    private HomeResponseDto createHomeResponseDto(LiveLectures lecture, LocalDate date,
        LocalTime startTime, LocalTime endTime, boolean isTeacher) {
        HomeResponseDto dto = new HomeResponseDto();
        dto.setLiveId(lecture.getLiveId());
        dto.setUserId(lecture.getUser().getId());
        dto.setNickname(lecture.getUser().getNickname());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());
        // 날짜 설정
        ZonedDateTime lectureDateTime = date.atStartOfDay(KOREA_ZONE);
        ZonedDateTime gmtLectureDateTime = lectureDateTime.withZoneSameInstant(ZoneOffset.UTC);
        dto.setLectureDate(gmtLectureDateTime.toInstant().toEpochMilli());
        // 시간 설정
        ZonedDateTime startDateTime = date.atTime(startTime).atZone(KOREA_ZONE);
        ZonedDateTime endDateTime = date.atTime(endTime).atZone(KOREA_ZONE);
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
    private List<HomeResponseDto> sortHomeData(List<HomeResponseDto> data) {
        return data.stream()
            .sorted(Comparator
                .comparingLong(HomeResponseDto::getLectureDate)
                .thenComparingLong(HomeResponseDto::getStartTime))
            .collect(Collectors.toList());
    }
}