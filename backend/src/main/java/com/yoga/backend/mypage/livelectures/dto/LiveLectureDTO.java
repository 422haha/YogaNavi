package com.yoga.backend.mypage.livelectures.dto;

import com.yoga.backend.common.entity.LiveLectures;
import java.time.Instant;

public class LiveLectureDTO {
    private Long liveId;
    private String liveTitle;
    private String liveContent;
    private Instant startDate;
    private Instant endDate;
    private Instant startTime;
    private Instant endTime;
    private Integer maxLiveNum;
    private String availableDay;
    private Instant regDate;
    private Integer userId;

    public Long getLiveId() {
        return liveId;
    }

    public void setLiveId(Long liveId) {
        this.liveId = liveId;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getLiveContent() {
        return liveContent;
    }

    public void setLiveContent(String liveContent) {
        this.liveContent = liveContent;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxLiveNum() {
        return maxLiveNum;
    }

    public void setMaxLiveNum(Integer maxLiveNum) {
        this.maxLiveNum = maxLiveNum;
    }

    public String getAvailableDay() {
        return availableDay;
    }

    public void setAvailableDay(String availableDay) {
        this.availableDay = availableDay;
    }

    public Instant getRegDate() {
        return regDate;
    }

    public void setRegDate(Instant regDate) {
        this.regDate = regDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public static LiveLectureDTO fromEntity(LiveLectures lecture) {
        LiveLectureDTO dto = new LiveLectureDTO();
        dto.setLiveId(lecture.getLiveId());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());
        dto.setStartDate(lecture.getStartDate());
        dto.setEndDate(lecture.getEndDate());
        dto.setStartTime(lecture.getStartTime());
        dto.setEndTime(lecture.getEndTime());
        dto.setMaxLiveNum(lecture.getMaxLiveNum());
        dto.setAvailableDay(lecture.getAvailableDay());
        dto.setRegDate(lecture.getRegDate());
        dto.setUserId(lecture.getUser().getId());
        return dto;
    }
}