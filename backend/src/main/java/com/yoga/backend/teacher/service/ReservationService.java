package com.yoga.backend.teacher.service;

import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.teacher.dto.ReservationRequestDto;

import java.util.List;

/**
 * 예약 서비스 인터페이스
 * 예약 생성 및 조회 등의 비즈니스 로직을 정의
 */
public interface ReservationService {

    /**
     * 예약 생성 메서드
     *
     * @param userId              사용자 ID
     * @param reservationRequest  예약 요청 DTO
     */
    void createReservation(int userId, ReservationRequestDto reservationRequest);

    /**
     * 특정 사용자의 예약 목록 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자의 예약 목록
     */
    List<MyLiveLecture> getUserReservations(int userId);

    /**
     * 특정 강의의 예약 목록 조회 메서드
     *
     * @param liveLectureId 실시간 강의 ID
     * @return 강의의 예약 목록
     */
    List<MyLiveLecture> getLiveLectureReservations(int liveLectureId);

    /**
     * 모든 실시간 강의 조회 메서드
     *
     * @param method 조회 방법
     * @return 모든 실시간 강의 목록
     */
    List<LiveLectureDto> getAllLiveLectures(int method);

    /**
     * 특정 강사의 예약 목록 조회 메서드
     *
     * @param teacherId 강사 ID
     * @return 강사의 예약 목록
     */
    List<MyLiveLecture> getReservationsByTeacher(int teacherId);

    /**
     * 특정 강사의 실시간 강의 조회 메서드
     *
     * @param teacherId 강사 ID
     * @param method    조회 방법
     * @return 강사의 실시간 강의 목록
     */
    List<LiveLectureDto> getLiveLecturesByTeacherAndMethod(int teacherId, int method);
}
