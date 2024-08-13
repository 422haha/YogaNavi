package com.yoga.backend.livelectures.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 홈 페이지 응답 DTO
 */
@Getter
@Setter
public class HomeResponseDto {

    private Long liveId;
    private String nickname;
    private String profileImageUrl;
    private String profileImageUrlSmall;
    private String liveTitle;
    private String liveContent;
    private Long startTime;
    private Long endTime;
    private Long lectureDate;
    private String lectureDay;
    private Integer maxLiveNum;
    private Boolean teacher;
    private Boolean isOnAir;

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }
}
