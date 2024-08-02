package com.yoga.backend.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 예약 엔티티 클래스
 * 사용자와 강사 간의 예약 정보를 담고 있습니다.
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user; // 사용자 (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_lecture_id")
    private LiveLectures liveLecture; // 실시간 강의 (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_date_id")
    private ReservationDate reservationDate; // 예약 날짜 (Foreign Key)

    // 기본 생성자
    public Reservation() {
    }

    /**
     * 예약을 생성하는 생성자
     *
     * @param user            사용자
     * @param liveLecture     실시간 강의
     * @param reservationDate 예약 날짜
     */
    public Reservation(Users user, LiveLectures liveLecture, ReservationDate reservationDate) {
        this.user = user;
        this.liveLecture = liveLecture;
        this.reservationDate = reservationDate;
    }
}
