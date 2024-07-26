package com.yoga.backend.mypage.livelectures.dto;

import lombok.Getter;
import lombok.Setter;
// 화상강의 생성후에 메시지만 응답하기 위해 사용함
@Setter
@Getter
public class LiveLectureCreateResponseDto {
    private String message;
    private Object data;
}
