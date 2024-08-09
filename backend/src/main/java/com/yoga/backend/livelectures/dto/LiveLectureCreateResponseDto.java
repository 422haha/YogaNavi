package com.yoga.backend.mypage.livelectures.dto;

import lombok.Getter;
import lombok.Setter;
/**
 * 화상 강의 생성 후 메시지와 데이터를 응답하기 위한 DTO 클래스
 * 이 클래스는 화상 강의 생성 성공 메시지와 함께 추가 데이터를 포함할 수 있습니다.
 */
@Setter
@Getter
public class LiveLectureCreateResponseDto {
    private String message; // 응답 메시지
    private Object data; // 응답 데이터
}
