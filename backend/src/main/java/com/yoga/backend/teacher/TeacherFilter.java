package com.yoga.backend.teacher;

/**
 * 강사 필터 클래스
 */
public class TeacherFilter {

    private int sorting;
    private long startTime;
    private long endTime;
    private String day;
    private int period;
    private int maxLiveNum;
    private String searchKeyword;

    // Getters and setters

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

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
