package com.yoga.backend.mypage.livelectures.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull; // 수정했어요 ㅍㅍ 죄송함다


/**
 * 실시간 강의 DTO(Data Transfer Object) 클래스. 강의 생성 및 관리를 위한 데이터 전송 객체
 */

@Setter
@Getter
public class LiveLectureCreateDto {
 // live id 추가? nickname 추가?


    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int liveId; // 화상 강의 ID

    @NotNull
    private String liveTitle; // 강의 제목

    @NotNull
    private String liveContent; // 강의 내용

    private Long startDate; // 시작 날짜
    private Long endDate; // 종료 날짜

    private Long startTime; // 강의 시작 시간
    private Long endTime; // 강의 종료 시간

    private Integer maxLiveNum; // 최대 수강자 수
    private Long regDate; // 강의 등록 시간

    @NotNull
    private Integer userId; // 강사 ID

//    private String nickname;

    @NotNull
    private String availableDay; // 가능한 강의 요일

}