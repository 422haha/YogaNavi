package com.yoga.backend.livelectures.service;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.livelectures.repository.LiveLectureRepository;
import com.yoga.backend.livelectures.repository.MyLiveLectureRepository;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.livelectures.dto.LectureHistoryDto;
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
 * 수강 내역
 */
@Service
public class HistoryServiceImpl implements HistoryService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final LiveLectureRepository liveLectureRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final UsersRepository usersRepository;

    public HistoryServiceImpl(
        LiveLectureRepository liveLectureRepository,
        MyLiveLectureRepository myLiveLectureRepository,
        UsersRepository usersRepository) {
        this.liveLectureRepository = liveLectureRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * 사용자 강의 이력 조회
     *
     * @param userId 사용자 ID
     * @return 강의 이력 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LectureHistoryDto> getHistory(int userId) {
        ZonedDateTime nowKorea = ZonedDateTime.now(KOREA_ZONE);

        List<LectureHistoryDto> result = new ArrayList<>();

        result.addAll(getPastUserLectures(userId, nowKorea));
        result.addAll(getPastStudentLectures(userId, nowKorea));

        return sortHistoryData(result);
    }

    /**
     * 학생의 과거 수강 이력
     */
    @Transactional(readOnly = true)
    public List<LectureHistoryDto> getPastStudentLectures(int userId, ZonedDateTime nowKorea) {
        LocalDate currentDate = nowKorea.toLocalDate();

        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findPastAndOngoingLecturesByUserId(
            userId, currentDate);

        List<LectureHistoryDto> result = new ArrayList<>();

        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            LiveLectures lecture = myLiveLecture.getLiveLecture();

            List<LectureHistoryDto> dtos = convertToLectureHistoryDto(lecture, myLiveLecture,
                nowKorea, false);

            for (LectureHistoryDto dto : dtos) {
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
    public List<LectureHistoryDto> getPastUserLectures(int userId, ZonedDateTime nowKorea) {
        LocalDate currentDate = nowKorea.toLocalDate();

        List<LiveLectures> lectures = liveLectureRepository.findPastAndOngoingLecturesByUser(userId,
            currentDate);

        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<LectureHistoryDto> result = new ArrayList<>();

        for (LiveLectures lecture : lectures) {

            List<LectureHistoryDto> dtos = convertToLectureHistoryDto(lecture, null, nowKorea,
                true);

            for (LectureHistoryDto dto : dtos) {
                // 강사의 프로필 이미지 URL 설정
                dto.setProfileImageUrl(user.getProfile_image_url());
                dto.setProfileImageUrlSmall(user.getProfile_image_url_small());
                result.add(dto);
            }
        }

        return result;
    }


    /**
     * LiveLectures Entity -> LectureHistoryDto
     */
    public List<LectureHistoryDto> convertToLectureHistoryDto(LiveLectures lecture,
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

        LocalTime startTime = ZonedDateTime.ofInstant(lecture.getStartTime(), ZoneId.of("UTC"))
            .toLocalTime();
        LocalTime endTime = ZonedDateTime.ofInstant(lecture.getEndTime(), ZoneId.of("UTC"))
            .toLocalTime();

        LocalDate today = nowKorea.toLocalDate();
        LocalTime nowTime = nowKorea.toLocalTime();

        boolean tillYesterday = endTime.isBefore(startTime);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            if (date.isAfter(today)) {
                break;
            }

            if (lecture.getAvailableDay()
                .contains(date.getDayOfWeek().toString().substring(0, 3))) {

                // 오늘 강의, 종료 시간이 지난 강의
                boolean isLectureToday =
                    date.isEqual(today) && endTime.isBefore(nowTime) && !tillYesterday;

                // 어제 시작된 강의가 오늘까지 이어짐, 이미 종료
                boolean isLectureYesterday =
                    date.equals(today.minusDays(1)) && tillYesterday && endTime.isBefore(nowTime);

                // 과거의 강의
                boolean isPastLecture = date.isBefore(today);

                if (isPastLecture || isLectureToday || isLectureYesterday) {
                    LectureHistoryDto dto = createLectureHistoryDto(lecture, date, startTime,
                        endTime, isTeacher);
                    dtos.add(dto);
                }
            }
        }

        return dtos;
    }

    /**
     * LectureHistoryDto 생성
     */
    public LectureHistoryDto createLectureHistoryDto(LiveLectures lecture, LocalDate date,
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
     * 내림차순 정렬
     */
    public List<LectureHistoryDto> sortHistoryData(List<LectureHistoryDto> data) {
        return data.stream()
            .sorted(Comparator
                .comparingLong(LectureHistoryDto::getLectureDate)
                .thenComparingLong(LectureHistoryDto::getStartTime)
                .reversed())
            .collect(Collectors.toList());
    }
}