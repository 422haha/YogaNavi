package com.yoga.backend.teacher.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 예약 요청을 위한 DTO 클래스
 */
@Data
@Builder
public class ReservationRequestDto {

    private String period; // 기간
    private int method; // 수업 방식 (0: 1대1, 1: 1대다)
    private Long lectureId; // 수강 가능한 수업 ID
}
