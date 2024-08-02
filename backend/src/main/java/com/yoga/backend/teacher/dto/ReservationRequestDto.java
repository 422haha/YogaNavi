package com.yoga.backend.teacher.dto;

/**
 * 예약 요청 DTO 클래스 실시간 강의 예약 요청에 사용됩니다.
 */
public class ReservationRequestDto {

    private int liveLectureId; // 실시간 강의 ID
    private Long startDate;    // 시작 날짜 (밀리초 단위)
    private Long endDate;      // 종료 날짜 (밀리초 단위)

    // getters and setters
    public int getLiveLectureId() {
        return liveLectureId;
    }

    public void setLiveLectureId(int liveLectureId) {
        this.liveLectureId = liveLectureId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
