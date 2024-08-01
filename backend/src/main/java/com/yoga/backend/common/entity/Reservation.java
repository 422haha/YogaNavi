package com.yoga.backend.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Users teacher;

    private LocalDateTime reservationTime;

    // Constructors, getters, setters

    public Reservation() {
    }

    public Reservation(Users user, Users teacher, LocalDateTime reservationTime) {
        this.user = user;
        this.teacher = teacher;
        this.reservationTime = reservationTime;
    }

    // Getters and setters
}
