package com.yoga.backend.teacher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 예약 요청 DTO 클래스 실시간 강의 예약 요청에 사용
 */
@Getter
@Setter
@Builder
public class ReservationRequestDto {

    private Integer liveId;     // 실시간 강의 ID
    private Long startDate;     // 시작 날짜 (밀리초 단위)
    private Long endDate;       // 종료 날짜 (밀리초 단위)
}
