package com.yoga.backend.teacher.dto;

public class ReservationRequestDto {
    private int liveLectureId;
    private Long reservationDateId;
    private Long startDate;  // 추가
    private Long endDate;    // 추가

    // getters and setters
    public int getLiveLectureId() {
        return liveLectureId;
    }

    public void setLiveLectureId(int liveLectureId) {
        this.liveLectureId = liveLectureId;
    }

    public Long getReservationDateId() {
        return reservationDateId;
    }

    public void setReservationDateId(Long reservationDateId) {
        this.reservationDateId = reservationDateId;
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
