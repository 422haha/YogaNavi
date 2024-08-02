package com.yoga.backend.mypage.livelectures.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


/**
 * 실시간 강의 DTO(Data Transfer Object) 클래스. 강의 생성 및 관리를 위한 데이터 전송 객체
 */

@Setter
@Getter
public class LiveLectureCreateDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liveId; // 화상 강의 ID

    private String liveTitle; // 강의 제목
    private String liveContent; // 강의 내용

    private Long startDate; // 시작 날짜 (밀리초 단위의 timestamp)
    private Long endDate; // 종료 날짜 (밀리초 단위의 timestamp)

    private Long startTime; // 강의 시작 시간 (밀리초 단위의 timestamp)
    private Long endTime; // 강의 종료 시간 (밀리초 단위의 timestamp)

    private Integer maxLiveNum; // 최대 수강자 수
    private Long regDate; // 강의 등록 시간 (밀리초 단위의 timestamp)

    private int userId; // 강사 ID (Integer에서 Long으로 변경)

    private String availableDay; // 가능한 강의 요일
}