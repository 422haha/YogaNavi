package com.yoga.backend.mypage.livelectures.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 화상 강의 응답을 위한 DTO 클래스
 * 화상 강의의 세부 정보를 포함
 */
@Setter
@Getter
public class LiveLectureResponseDto {

    private Integer liveId; // 화상 강의 ID
    private Long regDate; // 강의 등록 시간

    private int userId; // 강사 ID

    private String nickname; // 강사 이름
    private String profileImageUrl; // 강사 프로필 이미지 URL
    private String profileImageUrlSmall;  // 강사 프로필 이미지 URL 작은 사이즈

    private String liveTitle; // 강의 제목

    private String liveContent; // 강의 내용

    private String availableDay; // 가능한 강의 요일

    private Long startDate; // 시작 날짜
    private Long endDate; // 종료 날짜

    private Long startTime; // 강의 시작 시간
    private Long endTime; // 강의 종료 시간

    private Integer maxLiveNum; // 최대 수강자 수

}
