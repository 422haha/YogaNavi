package com.yoga.backend.mypage.livelectures;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LiveLectureResponseDto {

    @NotNull
    private Integer liveId; // 화상 강의 ID
    private Long regDate; // 강의 등록 시간

    @NotNull
    private int userId; // 강사 ID

    private String nickname; // 강사 이름
    private String profileImageUrl; // 강사 프사 경로

    @NotNull
    private String liveTitle; // 강의 제목

    @NotNull
    private String liveContent; // 강의 내용

    @NotNull
    private String availableDay; // 가능한 강의 요일

    private Long startDate; // 시작 날짜
    private Long endDate; // 종료 날짜

    private Long startTime; // 강의 시작 시간
    private Long endTime; // 강의 종료 시간

    private Integer maxLiveNum; // 최대 수강자 수

}
