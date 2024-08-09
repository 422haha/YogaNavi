package com.yoga.backend.home;

import lombok.Getter;
import lombok.Setter;


/**
 * 홈 페이지 응답 DTO 클래스. 홈 페이지에 대한 데이터 담고있음.
 */
@Getter
@Setter
public class HomeResponseDto {

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
    private Boolean isOnAir;

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }
}
