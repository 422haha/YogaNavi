package com.yoga.backend.teacher;

/**
 * 강사 필터 클래스
 */
public class TeacherFilter {

    private long startTime; // 강의 시작 시간
    private long endTime; // 강의 종료 시간
    private String day; // 강의 요일
    private int period; // 필터 기간
    private int maxLiveNum; // 최대 수강자 수
    private String searchKeyword; // 검색 키워드

    // Getters and setters...

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getMaxLiveNum() {
        return maxLiveNum;
    }

    public void setMaxLiveNum(int maxLiveNum) {
        this.maxLiveNum = maxLiveNum;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
