package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.service.S3Service;
import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.dto.LectureHistoryDto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  강의 내역 조회
 */
@Service
public class HistoryService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final long PRESIGNED_URL_EXPIRATION = 3600;

    private final LiveLectureRepository liveLectureRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final UsersRepository usersRepository;
    private final S3Service s3Service;

    public HistoryService(
        LiveLectureRepository liveLectureRepository,
        MyLiveLectureRepository myLiveLectureRepository,
        UsersRepository usersRepository,
        S3Service s3Service) {
        this.liveLectureRepository = liveLectureRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.usersRepository = usersRepository;
        this.s3Service = s3Service;
    }

    /**
     * 사용자 강의 이력 조회
     *
     * @param userId 사용자 ID
     * @return 강의 이력 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<LectureHistoryDto> getHistory(int userId) {
        ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);
        String dayOfWeek = nowKorea.getDayOfWeek().toString().substring(0, 3);

        List<LectureHistoryDto> result = new ArrayList<>();
        result.addAll(getPastUserLectures(userId, nowKorea, dayOfWeek));
        result.addAll(getPastStudentLectures(userId, nowKorea, dayOfWeek));

        return sortHistoryData(result);
    }

    /**
     * 학생의 과거 수강 이력
     */
    @Transactional(readOnly = true)
    protected List<LectureHistoryDto> getPastStudentLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        Instant currentDate = nowKorea.toInstant();
        Instant startDate = currentDate.minus(1, ChronoUnit.DAYS);
        Instant endDate = currentDate.plus(1, ChronoUnit.DAYS);

        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findPastAndOngoingLecturesByUserId(
            userId, startDate, currentDate, endDate, dayOfWeek);

        List<LectureHistoryDto> result = new ArrayList<>();

        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            LiveLectures lecture = myLiveLecture.getLiveLecture();
            List<LectureHistoryDto> dtos = convertToLectureHistoryDto(lecture, myLiveLecture,
                nowKorea, false);

            for (LectureHistoryDto dto : dtos) {
                Users teacher = lecture.getUser();
                dto.setProfileImageUrl(generatePresignedUrl(teacher.getProfile_image_url()));
                dto.setProfileImageUrlSmall(
                    generatePresignedUrl(teacher.getProfile_image_url_small()));
                result.add(dto);
            }
        }

        return result;
    }


    /**
     * 강사의 강의 이력
     */
    @Transactional(readOnly = true)
    protected List<LectureHistoryDto> getPastUserLectures(int userId, ZonedDateTime nowKorea,
        String dayOfWeek) {
        Instant currentDate = nowKorea.toInstant();
        Instant startDate = currentDate.minus(1, ChronoUnit.DAYS);
        Instant endDate = currentDate.plus(1, ChronoUnit.DAYS);

        List<LiveLectures> lectures = liveLectureRepository.findPastAndOngoingLecturesByUser(userId,
            startDate, currentDate, endDate, dayOfWeek);

        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<LectureHistoryDto> result = new ArrayList<>();

        for (LiveLectures lecture : lectures) {
            List<LectureHistoryDto> dtos = convertToLectureHistoryDto(lecture, null, nowKorea,
                true);

            for (LectureHistoryDto dto : dtos) {
                dto.setProfileImageUrl(generatePresignedUrl(user.getProfile_image_url()));
                dto.setProfileImageUrlSmall(
                    generatePresignedUrl(user.getProfile_image_url_small()));
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

    /**
     * LiveLectures Entity -> LectureHistoryDto
     */
/*    private List<LectureHistoryDto> convertToLectureHistoryDto(LiveLectures lecture,
        MyLiveLecture myLiveLecture, ZonedDateTime nowKorea, boolean isTeacher) {
        List<LectureHistoryDto> dtos = new ArrayList<>();

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

        System.out.println("lecture : " + lecture.getLiveTitle());
        System.out.println("lecture startDate : " + startDate);
        System.out.println("lecture endDate : " + endDate);
        System.out.println("lecture startTime : " + lecture.getStartTime());
        System.out.println("lecture endTime : " + lecture.getEndTime());

        LocalTime startTime = LocalTime.ofInstant(lecture.getStartTime(), KOREA_ZONE);
        LocalTime endTime = LocalTime.ofInstant(lecture.getEndTime(), KOREA_ZONE);

        LocalDate today = nowKorea.toLocalDate();
        LocalTime nowTime = nowKorea.toLocalTime();

        System.out.println("today : " + today);
        System.out.println("nowtime : " + nowTime);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (lecture.getAvailableDay()
                .contains(date.getDayOfWeek().toString().substring(0, 3))) {
                if (date.isBefore(today) || (date.isEqual(today) && endTime.isBefore(nowTime))) {
                    LectureHistoryDto dto = createLectureHistoryDto(lecture, date, startTime,
                        endTime, isTeacher);
                    dtos.add(dto);
                }
            }
        }

        return dtos;
    }*/
    private List<LectureHistoryDto> convertToLectureHistoryDto(LiveLectures lecture,
        MyLiveLecture myLiveLecture, ZonedDateTime nowKorea, boolean isTeacher) {
        List<LectureHistoryDto> dtos = new ArrayList<>();

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

//        System.out.println("lecture : " + lecture.getLiveTitle());
//        System.out.println("lecture startDate : " + startDate);
//        System.out.println("lecture endDate : " + endDate);
//        System.out.println("lecture startTime : " + lecture.getStartTime());
//        System.out.println("lecture endTime : " + lecture.getEndTime());

        LocalTime startTime = lecture.getStartTime().atZone(UTC_ZONE).toLocalTime();
        LocalTime endTime = lecture.getEndTime().atZone(UTC_ZONE).toLocalTime();

        LocalDate today = nowKorea.toLocalDate();
        LocalTime nowTime = nowKorea.toLocalTime();

//        System.out.println("today : " + today);
//        System.out.println("nowtime : " + nowTime);

        // 끝 날짜가 오늘 날짜 이전
        if (endDate.isBefore(today)) {
//            System.out.println("case1 : " + today + "   " + endDate);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                dtos.add(createLectureHistoryDto(lecture, date, startTime, endTime, isTeacher));
//                System.out.println("강의 추가 (case1): " + date);
            }
        }
        // 오늘 날짜가 startDate와 endDate 사이 (당일 포함)
        else if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
//            System.out.println("case2 : " + today + "   " + endDate);
            for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
//                System.out.println("현재 날짜: " + date);
//                System.out.println("비교: date.isBefore(today) = " + date.isBefore(today));
//                System.out.println("비교: date.isEqual(today) = " + date.isEqual(today));
//                System.out.println("비교: endTime = " + endTime);
//                System.out.println("비교: nowTime = " + nowTime);
//                System.out.println("비교: endTime.isBefore(nowTime) = " + endTime.isBefore(nowTime));

                if (date.isBefore(today) || (date.isEqual(today) && endTime.isBefore(nowTime))) {
                    dtos.add(createLectureHistoryDto(lecture, date, startTime, endTime, isTeacher));
                    System.out.println("강의 추가 (case2): " + date);
                }
            }
        }

        return dtos;
    }

    /**
     * LectureHistoryDto 생성
     */
    private LectureHistoryDto createLectureHistoryDto(LiveLectures lecture, LocalDate date,
        LocalTime startTime, LocalTime endTime, boolean isTeacher) {
        LectureHistoryDto dto = new LectureHistoryDto();

        dto.setLiveId(lecture.getLiveId());
        dto.setUserId(lecture.getUser().getId());
        dto.setNickname(lecture.getUser().getNickname());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());

        ZonedDateTime lectureDateTime = date.atStartOfDay(KOREA_ZONE);
        ZonedDateTime gmtLectureDateTime = lectureDateTime.withZoneSameInstant(ZoneOffset.UTC);
        dto.setLectureDate(gmtLectureDateTime.toInstant().toEpochMilli());

        ZonedDateTime startDateTime = date.atTime(startTime).atZone(UTC_ZONE).withZoneSameInstant(KOREA_ZONE);
        ZonedDateTime endDateTime = date.atTime(endTime).atZone(UTC_ZONE).withZoneSameInstant(KOREA_ZONE);

        dto.setStartTime(
            startDateTime.minusHours(9).toLocalTime().toNanoOfDay() / 1_000_000);
        dto.setEndTime(
            endDateTime.minusHours(9).toLocalTime().toNanoOfDay() / 1_000_000);

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
     * 내림차순 정렬
     */
    private List<LectureHistoryDto> sortHistoryData(List<LectureHistoryDto> data) {
        return data.stream()
            .sorted(Comparator
                .comparingLong(LectureHistoryDto::getLectureDate)
                .thenComparingLong(LectureHistoryDto::getStartTime)
                .reversed())
            .collect(Collectors.toList());
    }
}