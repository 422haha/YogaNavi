package com.yoga.backend.teacher.repository;

import com.yoga.backend.common.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(int userId);
    List<Reservation> findByTeacherId(int teacherId);
}
