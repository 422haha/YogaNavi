package com.yoga.backend.teacher.repository;

import com.yoga.backend.common.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(int userId);
    List<Reservation> findByLiveLecture_LiveId(long liveLectureId);
    List<Reservation> findByLiveLecture_UserId(int userId); // 강사의 ID로 예약 조회를 위한 메서드
}
