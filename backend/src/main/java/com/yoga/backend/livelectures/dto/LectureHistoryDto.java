package com.yoga.backend.livelectures.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureHistoryDto {

    private Long liveId;
    private String nickname;
    private String profileImageUrlSmall;
    private String liveTitle;
    private Long startTime;
    private Long endTime;
    private Long lectureDate;
    private String lectureDay;

}
