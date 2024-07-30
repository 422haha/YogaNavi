package com.yoga.backend.home;

import lombok.Getter;
import lombok.Setter;


/**
 * 홈 페이지 응답 DTO 클래스.
 * 홈 페이지에 대한 데이터를 담고 있습니다.
 */
@Getter
@Setter
public class HomeResponseDto {
    private Integer liveId; // 화상 강의 ID
    private Integer userId; // user ID
    private String nickname;    // 닉네임
    private String profileImageUrl;    // 프로필 이미지 URL
    private String profileImageUrlSmall;    // 작은 사이즈의 프로필 이미지 URL
    private String liveTitle;    // 라이브 강의 제목
    private String liveContent; // 라이브 강의 소개
    private Long startTime;    // 강의 시작 시간
    private Long endTime;    // 강의 종료 시간
    private Long lectureDate;    // 강의 날짜 (밀리초)
    private Long regDate;    // 강의 등록 날짜 (밀리초)
    private String lectureDay;    // 강의 요일
    private Integer maxLiveNum; // 최대 수강자 수
}
