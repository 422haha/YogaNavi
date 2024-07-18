package com.yoga.backend.mypage.recorded.dto;

import java.util.List;

public class LectureDto {

    private String email; // 강사 email
    private String title; // 강의 제목
    private String description; // 강의 소개
    private String thumbnailUrl; //썸네일 이미지 S3
    private List<ChapterDto> chapters;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public List<ChapterDto> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterDto> chapters) {
        this.chapters = chapters;
    }
}
