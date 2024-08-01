package com.yoga.backend.teacher.service;

import com.yoga.backend.common.entity.Reservation;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.teacher.dto.ReservationRequestDto;
import java.util.List;

/**
 * 예약 서비스를 위한 인터페이스
 */
public interface ReservationService {

    /**
     * 예약을 생성합니다.
     *
     * @param userId             사용자 ID
     * @param reservationRequest 예약 요청 DTO
     * @return 생성된 예약
     */
    Reservation createReservation(int userId, ReservationRequestDto reservationRequest);

    /**
     * 특정 사용자 ID로 예약 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    List<Reservation> getUserReservations(int userId);

    /**
     * 특정 강사 ID로 예약 목록을 조회합니다.
     *
     * @param teacherId 강사 ID
     * @return 예약 목록
     */
    List<Reservation> getTeacherReservations(int teacherId);

    /**
     * 모든 실시간 강의를 조회합니다.
     *
     * @param method 수업 방식 (0: 1대1, 1: 1대다)
     * @return 실시간 강의 목록
     */
    List<LiveLectureDto> getAllLiveLectures(int method);
}
