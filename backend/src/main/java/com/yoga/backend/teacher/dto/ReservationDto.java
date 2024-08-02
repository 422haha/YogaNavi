package com.yoga.backend.teacher.dto;

import com.yoga.backend.common.entity.Reservation;
import lombok.Builder;
import lombok.Data;

@Data
public class ReservationDto {
    private Long id;
    private int userId;
    private Long liveLectureId;
    private Long reservationDateId;

    @Builder
    public ReservationDto(Long id, int userId, Long liveLectureId, Long reservationDateId) {
        this.id = id;
        this.userId = userId;
        this.liveLectureId = liveLectureId;
        this.reservationDateId = reservationDateId;
    }

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUser().getId();
        this.liveLectureId = reservation.getLiveLecture().getLiveId();
        this.reservationDateId = reservation.getReservationDate().getId();
    }
}
