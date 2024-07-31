package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.fcm.NotificationService;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateResponseDto;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LiveLectureServiceImpl implements LiveLectureService {

    @Autowired
    private LiveLectureRepository liveLecturesRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LiveLectureCreateResponseDto createLiveLecture(
        LiveLectureCreateDto liveLectureCreateDto) {
        LiveLectures liveLecture = new LiveLectures();
        liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
        liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
        liveLecture.setStartDate(Instant.ofEpochMilli(liveLectureCreateDto.getStartDate()));
        liveLecture.setEndDate(Instant.ofEpochMilli(liveLectureCreateDto.getEndDate()));
        liveLecture.setStartTime(Instant.ofEpochMilli(liveLectureCreateDto.getStartTime()));
        liveLecture.setEndTime(Instant.ofEpochMilli(liveLectureCreateDto.getEndTime()));
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

                MyLiveLecture myLiveLecture = new MyLiveLecture();
                myLiveLecture.setLiveLecture(savedLiveLecture);
                myLiveLecture.setUser(user);
                myLiveLectureRepository.save(myLiveLecture);

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
     * 모든 실시간 강의를 조회
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
     * 사용자 ID로 화상 강의를 조회합니다.
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
     * 화상 강의를 수정합니다.
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
        return updatedLecture;
    }

    /**
     * 단일 화상 강의를 조회합니다.
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
     * 화상 강의의 소유자인지 확인합니다.
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
     * 화상 강의를 삭제합니다.
     *
     * @param liveId 화상 강의 ID
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteLiveLectureById(Long liveId) {
        notificationService.handleLectureDelete(liveId);

        List<MyLiveLecture> myLiveLectures = myLiveLectureRepository.findByLiveLecture_LiveId(
            liveId);
        myLiveLectureRepository.deleteAll(myLiveLectures);
        liveLecturesRepository.deleteById(liveId);
    }

}