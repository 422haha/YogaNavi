package com.yoga.backend.livelectures.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 화상 강의 생성 후 응답 DTO
 *
 */
@Setter
@Getter
public class LiveLectureCreateResponseDto {
    private String message; // 응답 메시지
    private Object data; // 응답 데이터
}
