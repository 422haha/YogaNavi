package com.yoga.backend.teacher.service;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.teacher.dto.ReservationRequestDto;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예약 서비스 구현 클래스 예약 생성 및 조회 등의 비즈니스 로직을 구현
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final MyLiveLectureRepository myLiveLectureRepository;
    private final UsersRepository usersRepository;
    private final LiveLectureRepository liveLectureRepository;

    @Autowired
    public ReservationServiceImpl(MyLiveLectureRepository myLiveLectureRepository,
        UsersRepository usersRepository,
        LiveLectureRepository liveLectureRepository) {
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.usersRepository = usersRepository;
        this.liveLectureRepository = liveLectureRepository;
    }

    /**
     * 예약 생성
     *
     * @param userId             사용자 ID
     * @param reservationRequest 예약 요청 DTO
     */
    @Override
    @Transactional
    public void createReservation(int userId, ReservationRequestDto reservationRequest) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LiveLectures liveLecture = liveLectureRepository.findById((long) reservationRequest.getLiveId())
            .orElseThrow(() -> new RuntimeException("실시간 강의를 찾을 수 없습니다."));

        // 최대 인원수 확인
        int currentParticipants = myLiveLectureRepository.countByLiveLectureAndEndDateAfter(liveLecture, Instant.now());
        if (currentParticipants >= liveLecture.getMaxLiveNum()) {
            throw new RuntimeException("최대 인원수를 초과하였습니다.");
        }

        MyLiveLecture myLiveLecture = new MyLiveLecture();
        myLiveLecture.setUser(user);
        myLiveLecture.setLiveLecture(liveLecture);
        myLiveLecture.setStartDate(Instant.ofEpochMilli(reservationRequest.getStartDate()));
        myLiveLecture.setEndDate(Instant.ofEpochMilli(reservationRequest.getEndDate()));

        myLiveLectureRepository.save(myLiveLecture);
    }

    /**
     * 사용자 예약 조회
     *
     * @param userId 사용자 ID
     * @return 사용자의 예약 목록
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<MyLiveLecture> getUserReservations(int userId) {
        return myLiveLectureRepository.findByUserId(userId);
    }

    /**
     * 실시간 강의 예약 조회
     *
     * @param liveId 실시간 강의 ID
     * @return 실시간 강의의 예약 목록
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<MyLiveLecture> getLiveLectureReservations(int liveId) {
        return myLiveLectureRepository.findByLiveLecture_LiveId((long) liveId);
    }

    /**
     * 모든 실시간 강의 조회
     *
     * @param method 조회 방법 (0: 최대 수강자 수가 1인 강의, 1: 그 외)
     * @return 실시간 강의 목록
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<LiveLectureDto> getAllLiveLectures(int method) {
        Instant now = Instant.now();
        List<LiveLectures> lectures;

        if (method == 0) {
            lectures = liveLectureRepository.findAllByMaxLiveNumAndEndDateAfter(1, now);
        } else {
            lectures = liveLectureRepository.findAllByMaxLiveNumGreaterThanAndEndDateAfter(1, now);
        }

        return lectures.stream()
            .filter(lecture -> {
                int currentParticipants = myLiveLectureRepository.countByLiveLectureAndEndDateAfter(lecture, now);
                return currentParticipants < lecture.getMaxLiveNum();
            })
            .map(LiveLectureDto::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 강사별 실시간 강의 조회
     *
     * @param teacherId 강사 ID
     * @param method    조회 방법 (0: 최대 수강자 수가 1인 강의, 1: 그 외)
     * @return 강사의 실시간 강의 목록
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<LiveLectureDto> getLiveLecturesByTeacherAndMethod(int teacherId, int method) {
        Instant now = Instant.now();
        List<LiveLectures> lectures;

        if (method == 0) {
            lectures = liveLectureRepository.findByUserIdAndMaxLiveNumAndEndDateAfter(teacherId, 1, now);
        } else {
            lectures = liveLectureRepository.findByUserIdAndMaxLiveNumGreaterThanAndEndDateAfter(teacherId, 1, now);
        }

        return lectures.stream()
            .filter(lecture -> {
                int currentParticipants = myLiveLectureRepository.countByLiveLectureAndEndDateAfter(lecture, now);
                return currentParticipants < lecture.getMaxLiveNum();
            })
            .map(LiveLectureDto::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 강사 예약 조회
     *
     * @param teacherId 강사 ID
     * @return 강사의 예약 목록
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<MyLiveLecture> getReservationsByTeacher(int teacherId) {
        return myLiveLectureRepository.findByLiveLectureWithUser((long) teacherId);
    }
}
