package com.yoga.backend.home;

import lombok.Getter;
import lombok.Setter;


/**
 * 홈 페이지 응답 DTO 클래스.
 * 홈 페이지에 대한 데이터를 담고 있습니다.
 */
@Getter
@Setter
public class HomeResponseDto {
    private Long liveId;
    private int userId;
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
}
