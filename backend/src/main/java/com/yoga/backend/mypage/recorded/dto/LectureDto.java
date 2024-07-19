package com.yoga.backend.mypage.recorded.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureDto {

    private Long recordedId; // Changed to Long
    private String email;
    private String recordTitle;
    private String recordContent;
    private String recordThumbnail;
    private List<ChapterDto> recordedLectureChapters;
    private int likeCount;
    private boolean myLike;
}
