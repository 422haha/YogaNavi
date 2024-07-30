package com.yoga.backend.home;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
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

/**
 * 홈 서비스 클래스.
 * 홈 페이지에 대한 비즈니스 로직을 처리합니다.
 */
@Service
public class HomeService {

    // JWT 유틸리티 클래스 인스턴스를 주입받습니다.
    @Autowired
    private JwtUtil jwtUtil;

    // 사용자 리포지토리 인스턴스를 주입받습니다.
    @Autowired
    private UsersRepository usersRepository;

    // 내 라이브 강의 리포지토리 인스턴스를 주입받습니다.
    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;

    // 라이브 강의 리포지토리 인스턴스를 주입받습니다.
    @Autowired
    private LiveLectureRepository liveLectureRepository;

    /**
     * 주어진 사용자 ID에 해당하는 홈 데이터 리스트를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 홈 응답 DTO 리스트
     */
    public List<HomeResponseDto> getHomeData(Integer userId) {
        // 현재 시간을 가져옵니다.
        long now = ZonedDateTime.now().toInstant().toEpochMilli();

        // 사용자 ID로 내 라이브 강의 리스트를 조회합니다.
        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findByUserId(userId);
        // 홈 응답 DTO 리스트를 생성합니다.
        List<HomeResponseDto> responseList = new ArrayList<>();

        // 내 라이브 강의 리스트를 순회합니다.
        for (MyLiveLecture myLiveLecture : myLiveLectures) {
            // 라이브 강의 ID로 라이브 강의를 조회합니다.
            LiveLectures liveLecture = liveLectureRepository.findById(myLiveLecture.getLiveId()).orElse(null);

            if (liveLecture != null) {
                // 라이브 강의의 시작일과 종료일, 가능한 요일로 강의 날짜 리스트를 가져옵니다.
                List<String> lectureDates = getLectureDates(liveLecture.getStartDate(), liveLecture.getEndDate(), liveLecture.getAvailableDay());
                for (String date : lectureDates) {
                    long lectureDateLong = convertToLong(date);
                    // 현재 시간 이후의 강의만 추가합니다.
                    if (lectureDateLong >= now) {
                        // 새로운 홈 응답 DTO를 생성하고 데이터를 설정합니다.
                        HomeResponseDto dto = new HomeResponseDto();
                        dto.setLiveId(liveLecture.getLiveId());
                        dto.setUserId(liveLecture.getUser().getId());
                        dto.setNickname(liveLecture.getUser().getNickname());
                        dto.setProfileImageUrl(liveLecture.getUser().getProfile_image_url());
                        dto.setProfileImageUrlSmall(
                            liveLecture.getUser().getProfile_image_url_small());
                        dto.setLiveTitle(liveLecture.getLiveTitle());
                        dto.setLiveContent(liveLecture.getLiveContent());
                        dto.setStartTime(liveLecture.getStartTime());
                        dto.setEndTime(liveLecture.getEndTime());
                        dto.setLectureDate(convertToLong(date));
                        dto.setRegDate(liveLecture.getRegDate());
                        dto.setMaxLiveNum(liveLecture.getMaxLiveNum());
                        dto.setLectureDay(LocalDate.parse(date).getDayOfWeek()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
                        responseList.add(dto);
                    }
                }
            }
        }
        // 날짜와 시간 순서대로 정렬합니다.
        responseList.sort(Comparator.comparing(HomeResponseDto::getLectureDate).thenComparing(HomeResponseDto::getStartTime));


        return responseList;
    }

    /**
     * JWT 토큰을 사용하여 사용자의 라이브 강의 리스트를 가져옵니다.
     *
     * @param token JWT 토큰
     * @return 홈 응답 DTO 리스트
     */
    public List<HomeResponseDto> getMyLiveLectures(String token) {
        // 현재 시간을 가져옵니다.
        long now = ZonedDateTime.now().toInstant().toEpochMilli();

        // 토큰에서 사용자 ID를 추출합니다.
        Integer userId = jwtUtil.getUserIdFromToken(token);
        // 사용자 ID로 사용자를 조회합니다.
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자 ID로 내 라이브 강의 리스트를 조회합니다.
        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findByUserId(userId);

        return myLiveLectures.stream().map(myLiveLecture -> {
            // 라이브 강의 ID로 라이브 강의를 조회합니다.
            LiveLectures lecture = liveLectureRepository.findById(myLiveLecture.getLiveId()).orElseThrow(() -> new RuntimeException("Lecture not found"));

            // 라이브 강의의 시작일과 종료일, 가능한 요일로 강의 날짜 리스트를 가져옵니다.
            List<String> lectureDates = getLectureDates(lecture.getStartDate(), lecture.getEndDate(), lecture.getAvailableDay());

            return lectureDates.stream().map(lectureDate -> {
                // 새로운 홈 응답 DTO를 생성하고 데이터를 설정합니다.
                HomeResponseDto dto = new HomeResponseDto();
                dto.setNickname(user.getNickname());
                dto.setProfileImageUrl(user.getProfile_image_url());
                dto.setProfileImageUrlSmall(user.getProfile_image_url_small());
                dto.setLiveTitle(lecture.getLiveTitle());
                dto.setStartTime(lecture.getStartTime());
                dto.setEndTime(lecture.getEndTime());
                dto.setLectureDate(convertToLong(lectureDate));
                dto.setLectureDay(LocalDate.parse(lectureDate).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase());
                return dto;
            }).collect(Collectors.toList());
        }).flatMap(List::stream).collect(Collectors.toList());
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
     * 날짜 문자열을 사용하여 요일 문자열을 반환합니다.
     *
     * @param date 날짜 문자열 (yyyy-MM-dd 형식)
     * @return 요일 문자열
     */
    private String getDayOfWeek(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return localDate.getDayOfWeek().toString();
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

