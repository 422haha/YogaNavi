package com.yoga.backend.mypage.recorded.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LectureCreationStatus {
    private String status;
    private String message;
    private LectureDto lectureDto;

    public LectureCreationStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }
}