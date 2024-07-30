package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 홈 서비스 클래스.
 * 홈 페이지에 대한 비즈니스 로직을 처리합니다.
 */
@Service
public class HomeService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;

    @Autowired
    private LiveLectureRepository liveLectureRepository;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<HomeResponseDto> getHomeData(int userId) {
        long now = Instant.now().toEpochMilli();

        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findByUserId(userId);
        List<HomeResponseDto> responseList = new ArrayList<>();

        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            LiveLectures liveLecture = liveLectureRepository.findById(myLiveLecture.getLiveId()).orElse(null);

            if (liveLecture != null) {
                List<String> lectureDates = getLectureDates(liveLecture.getStartDate().toEpochMilli(),
                    liveLecture.getEndDate().toEpochMilli(),
                    liveLecture.getAvailableDay());
                for (String date : lectureDates) {
                    long lectureDateLong = convertToLong(date);
                    if (lectureDateLong >= now) {
                        HomeResponseDto dto = new HomeResponseDto();
                        dto.setLiveId(liveLecture.getLiveId());
                        dto.setUserId(liveLecture.getUser().getId());
                        dto.setNickname(liveLecture.getUser().getNickname());
                        dto.setProfileImageUrl(liveLecture.getUser().getProfile_image_url());
                        dto.setProfileImageUrlSmall(liveLecture.getUser().getProfile_image_url_small());
                        dto.setLiveTitle(liveLecture.getLiveTitle());
                        dto.setLiveContent(liveLecture.getLiveContent());
                        dto.setStartTime(liveLecture.getStartTime().toEpochMilli());
                        dto.setEndTime(liveLecture.getEndTime().toEpochMilli());
                        dto.setLectureDate(lectureDateLong);
                        dto.setRegDate(liveLecture.getRegDate().toEpochMilli());
                        dto.setMaxLiveNum(liveLecture.getMaxLiveNum());
                        dto.setLectureDay(LocalDate.parse(date).getDayOfWeek()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
                        responseList.add(dto);
                    }
                }
            }
        }
        responseList.sort(Comparator.comparing(HomeResponseDto::getLectureDate).thenComparing(HomeResponseDto::getStartTime));

        return responseList;
    }

    /**
     * 시작일과 종료일, 가능한 요일로 강의 날짜 리스트를 생성합니다.
     *
     * @param startDate    시작일 (밀리초)
     * @param endDate      종료일 (밀리초)
     * @param availableDays 가능한 요일 (쉼표로 구분된 문자열)
     * @return 강의 날짜 리스트
     */
    private List<String> getLectureDates(Long startDate, Long endDate, String availableDays) {
        LocalDate start = Instant.ofEpochMilli(startDate).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Instant.ofEpochMilli(endDate).atZone(ZoneId.systemDefault()).toLocalDate();
        List<DayOfWeek> daysOfWeek = parseAvailableDays(availableDays);

        List<String> lectureDates = new ArrayList<>();
        LocalDate date = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!date.isAfter(end)) {
            if (daysOfWeek.contains(date.getDayOfWeek())) {
                lectureDates.add(date.format(formatter));
            }
            date = date.plusDays(1);
        }

        return lectureDates;
    }

    /**
     * 가능한 요일 문자열을 파싱하여 DayOfWeek 리스트로 변환합니다.
     *
     * @param availableDays 가능한 요일 (쉼표로 구분된 문자열)
     * @return DayOfWeek 리스트
     */
    private List<DayOfWeek> parseAvailableDays(String availableDays) {
        return Arrays.stream(availableDays.split(","))
            .map(day -> {
                switch (day.trim().toUpperCase()) {
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
                        throw new IllegalArgumentException("Invalid day of week: " + day);
                }
            })
            .toList();
    }


    /**
     * 날짜 문자열을 밀리초 값으로 변환합니다.
     *
     * @param date 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 밀리초 값 (Long)
     */
    private Long convertToLong(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}

