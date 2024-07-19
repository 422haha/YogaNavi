package com.yoga.backend.mypage.recorded.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterDto {

    private String chapterTitle;
    private String chapterDescription;
    private String thumbnailUrl;
    private String videoUrl;
    private int chapterNumber;
}