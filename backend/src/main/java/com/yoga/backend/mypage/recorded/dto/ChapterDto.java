package com.yoga.backend.mypage.recorded.dto;

public class ChapterDto {

    private String title; // 챕터 제목
    private String description; // 챕터 소개
    private String videoUrl; // 영상의 s3 url
    private int order; // 챕터 순서

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
