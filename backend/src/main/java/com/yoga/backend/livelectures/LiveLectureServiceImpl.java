package com.yoga.backend.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.fcm.NotificationService;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.livelectures.dto.LiveLectureCreateResponseDto;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiveLectureServiceImpl implements LiveLectureService {

    private final LiveLectureRepository liveLecturesRepository;
    private final UsersRepository usersRepository;
    private final MyLiveLectureRepository myLiveLectureRepository;
    private final NotificationService notificationService;

    public LiveLectureServiceImpl(LiveLectureRepository liveLecturesRepository,
        UsersRepository usersRepository, MyLiveLectureRepository myLiveLectureRepository,
        NotificationService notificationService) {
        this.liveLecturesRepository = liveLecturesRepository;
        this.usersRepository = usersRepository;
        this.myLiveLectureRepository = myLiveLectureRepository;
        this.notificationService = notificationService;
    }

    /**
     * 라이브 강의 생성
     *
     * @param liveLectureCreateDto 생성할 강의 정보
     * @return 강의 생성 결과
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LiveLectureCreateResponseDto createLiveLecture(
        LiveLectureCreateDto liveLectureCreateDto) {

        LiveLectures liveLecture = new LiveLectures();
        liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
        liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
        liveLecture.setStartDate(Instant.ofEpochMilli(liveLectureCreateDto.getStartDate()));//하루 앞
        liveLecture.setEndDate(Instant.ofEpochMilli(liveLectureCreateDto.getEndDate()));// 하루 앞
        liveLecture.setStartTime(Instant.ofEpochMilli(liveLectureCreateDto.getStartTime()));//한국 시간
        liveLecture.setEndTime(Instant.ofEpochMilli(liveLectureCreateDto.getEndTime()));// 한국 시간
        liveLecture.setMaxLiveNum(liveLectureCreateDto.getMaxLiveNum());
        liveLecture.setRegDate(Instant.now());
        liveLecture.setAvailableDay(liveLectureCreateDto.getAvailableDay());

        if (liveLectureCreateDto.getUserId() != 0) {
            Optional<Users> userOptional = usersRepository.findById(
                liveLectureCreateDto.getUserId());
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                liveLecture.setUser(user);

                LiveLectures savedLiveLecture = liveLecturesRepository.save(liveLecture);
                notificationService.handleLectureUpdate(savedLiveLecture);

                LiveLectureCreateResponseDto responseDto = new LiveLectureCreateResponseDto();
                responseDto.setMessage("화상강의 생성 성공");
                responseDto.setData(null);
                return responseDto;

            } else {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }
        } else {
            throw new IllegalArgumentException("사용자 ID는 0일 수 없습니다");
        }
    }

    /**
     * 모든 실시간 강의 조회
     *
     * @return 모든 실시간 강의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LiveLectures> getAllLiveLectures() {
        return liveLecturesRepository.findAll();
    }

    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyLiveLecture> getMyLiveLecturesByUserId(int userId) {
        return myLiveLectureRepository.findByUserId(userId);
    }

    /**
     * 사용자 ID로 화상 강의를 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 실시간 강의 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LiveLectures> getLiveLecturesByUserId(int userId) {
        return liveLecturesRepository.findByUserId(userId);
    }

    /**
     * 화상 강의를 수정
     *
     * @param liveLectureCreateDto 수정할 화상 강의 DTO
     * @return 수정된 화상 강의 엔티티
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LiveLectures updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto) {

        LiveLectures liveLecture = liveLecturesRepository.findById(liveLectureCreateDto.getLiveId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid lecture ID"));

        if (liveLectureCreateDto.getLiveTitle() != null) {
            liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
        }
        if (liveLectureCreateDto.getLiveContent() != null) {
            liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
        }
        if (liveLectureCreateDto.getStartDate() != null) {
            liveLecture.setStartDate(Instant.ofEpochMilli(liveLectureCreateDto.getStartDate()));
        }
        if (liveLectureCreateDto.getEndDate() != null) {
            liveLecture.setEndDate(Instant.ofEpochMilli(liveLectureCreateDto.getEndDate()));
        }
        if (liveLectureCreateDto.getStartTime() != null) {
            liveLecture.setStartTime(Instant.ofEpochMilli(liveLectureCreateDto.getStartTime()));
        }
        if (liveLectureCreateDto.getEndTime() != null) {
            liveLecture.setEndTime(Instant.ofEpochMilli(liveLectureCreateDto.getEndTime()));
        }
        if (liveLectureCreateDto.getMaxLiveNum() != null) {
            liveLecture.setMaxLiveNum(liveLectureCreateDto.getMaxLiveNum());
        }
        if (liveLectureCreateDto.getAvailableDay() != null) {
            liveLecture.setAvailableDay(liveLectureCreateDto.getAvailableDay());
        }

        LiveLectures updatedLecture = liveLecturesRepository.save(liveLecture);
        notificationService.handleLectureUpdate(updatedLecture);

        notificationService.sendLectureUpdateNotification(updatedLecture);

        return updatedLecture;
    }

    /**
     * 단일 화상 강의 조회
     *
     * @param liveId 화상 강의 ID
     * @return 해당 화상 강의 엔티티
     */
    @Override
    @Transactional(readOnly = true)
    public LiveLectures getLiveLectureById(Long liveId) {
        return liveLecturesRepository.findById(liveId).orElse(null);
    }

    /**
     * 화상 강의의 소유자인지 확인
     *
     * @param liveId 화상 강의 ID
     * @param userId 사용자 ID
     * @return 소유자 여부
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isLectureOwner(Long liveId, int userId) {
        Optional<LiveLectures> lectureOpt = liveLecturesRepository.findById(liveId);
        return lectureOpt.isPresent() && Objects.equals(lectureOpt.get().getUser().getId(), userId);
    }

    /**
     * 화상 강의 삭제
     *
     * @param liveId 화상 강의 ID
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteLiveLectureById(Long liveId) {
        try {
            LiveLectures lecture = liveLecturesRepository.findById(liveId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 강의 id: " + liveId));

            List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findByLiveLecture_LiveId(
                liveId);
            myLiveLectureRepository.deleteAll(myLiveLectures);

            liveLecturesRepository.delete(lecture);

            notificationService.handleLectureDelete(liveId);

            notificationService.sendLectureDeletionNotification(lecture, myLiveLectures);

        } catch (Exception e) {
            throw new RuntimeException("강의 삭제 실패", e);
        }
    }

    // 비동기 처리 코드
    //    @Async
//    @Override
//    public CompletableFuture<Void> updateIsOnAir(Long liveId, boolean isOnAir) {
//        LiveLectures lecture = liveLecturesRepository.findById(liveId)
//            .orElseThrow(() -> new IllegalArgumentException("Invalid liveId: " + liveId));
//        lecture.setIsOnAir(isOnAir);
//        liveLecturesRepository.save(lecture);
//        return CompletableFuture.completedFuture(null);
//    }

    /**
     * 실시간 강의의 OnAir 상태를 업데이트합니다.
     *
     * @param liveId 업데이트할 강의의 ID
     * @param isOnAir 강의가 OnAir 상태인지 여부
     */
    @Override
    public void updateIsOnAir(Long liveId, boolean isOnAir) {
        // liveId로 강의를 검색하고, 존재하지 않으면 예외를 발생
        LiveLectures lecture = liveLecturesRepository.findById(liveId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid liveId: " + liveId));

        // OnAir 상태 설절하고 저장
        lecture.setIsOnAir(isOnAir);
        liveLecturesRepository.save(lecture);
    }
}

