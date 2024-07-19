package com.yoga.backend.mypage.recorded.dto;


import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureDto {

    private String recorded_id;
    private String email; // 강사 email
    private String record_title; // 강의 제목
    private String record_content; // 강의 소개
    private String record_thumbnail; //썸네일 이미지 S3
    private List<ChapterDto> RecordedLectureChapter;
    private int like_count;
    private boolean my_like;

}

