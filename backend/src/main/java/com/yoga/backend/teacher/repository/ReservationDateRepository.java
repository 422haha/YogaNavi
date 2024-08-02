package com.yoga.backend.teacher.repository;

import com.yoga.backend.common.entity.ReservationDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationDateRepository extends JpaRepository<ReservationDate, Long> {
}
