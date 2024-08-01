package com.yoga.backend.teacher.service;

import com.yoga.backend.common.entity.Reservation;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.teacher.dto.ReservationRequestDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.teacher.repository.ReservationRepository;
import com.yoga.backend.members.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 서비스 구현 클래스
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
    private final LiveLectureRepository liveLectureRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
        UsersRepository usersRepository,
        LiveLectureRepository liveLectureRepository) {
        this.reservationRepository = reservationRepository;
        this.usersRepository = usersRepository;
        this.liveLectureRepository = liveLectureRepository;
    }

    /**
     * 예약을 생성합니다.
     *
     * @param userId             사용자 ID
     * @param reservationRequest 예약 요청 DTO
     * @return 생성된 예약
     */
    @Override
    @Transactional
    public Reservation createReservation(int userId, ReservationRequestDto reservationRequest) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Users teacher = usersRepository.findById(reservationRequest.getLectureId())
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));

        Reservation reservation = new Reservation(user, teacher, LocalDateTime.now());
        return reservationRepository.save(reservation);
    }

    /**
     * 특정 사용자 ID로 예약 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getUserReservations(int userId) {
        return reservationRepository.findByUserId(userId);
    }

    /**
     * 특정 강사 ID로 예약 목록을 조회합니다.
     *
     * @param teacherId 강사 ID
     * @return 예약 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getTeacherReservations(int teacherId) {
        return reservationRepository.findByTeacherId(teacherId);
    }

    /**
     * 모든 실시간 강의를 조회합니다.
     *
     * @param method 수업 방식 (0: 1대1, 1: 1대다)
     * @return 실시간 강의 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<LiveLectureDto> getAllLiveLectures(int method) {
        if (method == 0) {
            // 1대1 강의 조회
            return liveLectureRepository.findAllByMaxLiveNum(1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        } else {
            // 1대다 강의 조회
            return liveLectureRepository.findAllByMaxLiveNumGreaterThan(1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        }
    }
}
