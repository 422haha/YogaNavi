package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.UsersRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 실시간 강의 서비스 구현 클래스입니다.
 * 실시간 강의 관련 비즈니스 로직을 구현합니다.
 */
@Service
public class LiveLectureServiceImpl implements LiveLectureService {

    @Autowired
    private LiveLectureRepository liveLecturesRepository;// 화상 강의 저장소

    @Autowired
    private UsersRepository usersRepository;// 사용자 저장소

    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;// 사용자의 화상 강의 목록 저장소

    @Autowired
    private LiveLectureRepository liveLectureRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 실시간 강의를 생성합니다.
     *
     * @param liveLectureCreateDto 실시간 강의 DTO
     * @return 생성된 실시간 강의 엔티티
     */
    @Override
    public LiveLectureCreateResponseDto createLiveLecture(LiveLectureCreateDto liveLectureCreateDto) {
        LiveLectures liveLecture = new LiveLectures();
        liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
        liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
        liveLecture.setStartDate(liveLectureCreateDto.getStartDate());
        liveLecture.setEndDate(liveLectureCreateDto.getEndDate());
        liveLecture.setStartTime(liveLectureCreateDto.getStartTime());
        liveLecture.setEndTime(liveLectureCreateDto.getEndTime());
        liveLecture.setMaxLiveNum(liveLectureCreateDto.getMaxLiveNum());
        liveLecture.setRegDate(System.currentTimeMillis());
        liveLecture.setAvailableDay(liveLectureCreateDto.getAvailableDay());

        if (liveLectureCreateDto.getUserId() != null) {
            Optional<Users> userOptional = usersRepository.findById(
                (long) liveLectureCreateDto.getUserId());
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                liveLecture.setUser(user);

                LiveLectures savedLiveLecture = liveLecturesRepository.save(liveLecture);

                MyLiveLecture myLiveLecture = new MyLiveLecture();
                myLiveLecture.setLiveLecture(savedLiveLecture);
                myLiveLecture.setUser(user);
                myLiveLectureRepository.save(myLiveLecture);

                LiveLectureCreateResponseDto responseDto = new LiveLectureCreateResponseDto();
                responseDto.setMessage("화상강의 생성 성공");
                responseDto.setData(null);
                return responseDto;

//                return savedLiveLecture;
            } else {
                // 사용자 정보가 없을 경우 처리 로직 추가 필요
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }
        } else {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다");
        }
    }

    /**
     * 모든 실시간 강의를 조회
     * @return 모든 실시간 강의 리스트
     */
    @Override
    public List<LiveLectures> getAllLiveLectures() {
        return liveLecturesRepository.findAll();
    } // 모든 화상 강의를 조회
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    @Override
    public List<MyLiveLecture> getMyLiveLecturesByUserId(Integer userId) {
        return myLiveLectureRepository.findByUserId(userId);  // 특정 사용자가 등록한 화상 강의 목록을 조회
    }

    @Override
    public List<LiveLectures> getLiveLecturesByUserId(Integer userId) {
        return liveLectureRepository.findByUserId(userId);
    }

    @Override
    public LiveLectures updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto) {
        LiveLectures liveLecture = liveLectureRepository.findById(liveLectureCreateDto.getLiveId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid lecture ID"));

        if (liveLectureCreateDto.getLiveTitle() != null) {
            liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
        }
        if (liveLectureCreateDto.getLiveContent() != null) {
            liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
        }
        if (liveLectureCreateDto.getStartDate() != null) {
            liveLecture.setStartDate(liveLectureCreateDto.getStartDate());
        }
        if (liveLectureCreateDto.getEndDate() != null) {
            liveLecture.setEndDate(liveLectureCreateDto.getEndDate());
        }
        if (liveLectureCreateDto.getStartTime() != null) {
            liveLecture.setStartTime(liveLectureCreateDto.getStartTime());
        }
        if (liveLectureCreateDto.getEndTime() != null) {
            liveLecture.setEndTime(liveLectureCreateDto.getEndTime());
        }
        if (liveLectureCreateDto.getMaxLiveNum() != null) {
            liveLecture.setMaxLiveNum(liveLectureCreateDto.getMaxLiveNum());
        }
        if (liveLectureCreateDto.getAvailableDay() != null) {
            liveLecture.setAvailableDay(liveLectureCreateDto.getAvailableDay());
        }

        return liveLectureRepository.save(liveLecture);
    }

    //단일화상강의조회
    @Override
    public LiveLectures getLiveLectureById(Integer liveId) {
        return liveLectureRepository.findById(liveId).orElse(null);
    }

    // 본인 맞는지 확인
    @Override
    public boolean isLectureOwner(Integer liveId, Integer userId) {
        Optional<LiveLectures> lectureOpt = liveLectureRepository.findById(liveId);
        return lectureOpt.isPresent() && Objects.equals(lectureOpt.get().getUser().getId(), userId);
    }

    //삭제
    @Override
    public void deleteLiveLectureById(Integer liveId) {
        liveLectureRepository.deleteById(liveId);
    }

}