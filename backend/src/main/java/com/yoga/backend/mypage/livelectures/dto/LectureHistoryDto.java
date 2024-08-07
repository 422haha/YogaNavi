package com.yoga.backend.mypage.livelectures.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureHistoryDto {

    private Long liveId;
    private Integer userId;
    private String nickname;
    private String profileImageUrl;
    private String profileImageUrlSmall;
    private String liveTitle;
    private String liveContent;
    private Long startTime;
    private Long endTime;
    private Long lectureDate;
    private Long regDate;
    private String lectureDay;
    private Integer maxLiveNum;
    private Boolean teacher;

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }
}
