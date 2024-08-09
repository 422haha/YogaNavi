package com.yoga.backend.livelectures.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 실시간 강의 DTO.
 */
@Setter
@Getter
public class LiveLectureCreateDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liveId; // 화상 강의 ID

    private String liveTitle; // 강의 제목
    private String liveContent; // 강의 내용

    private Long startDate; // 시작 날짜
    private Long endDate; // 종료 날짜

    private Long startTime; // 강의 시작 시간
    private Long endTime; // 강의 종료 시간

    private Integer maxLiveNum; // 최대 수강자 수
    private Long regDate; // 강의 등록 시간

    private Integer userId; // 강사 ID

    private String availableDay; // 가능한 강의 요일
}