package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import com.yoga.backend.mypage.mylivelecture.MyLiveLectureRepository;
import java.util.List;
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
    /**
     * 실시간 강의를 생성합니다.
     * @param liveLectureDto 실시간 강의 DTO
     * @return 생성된 실시간 강의 엔티티
     */
    @Override
    public LiveLectures createLiveLecture(LiveLectureDto liveLectureDto) {
        LiveLectures liveLecture = new LiveLectures();
        liveLecture.setLiveTitle(liveLectureDto.getLiveTitle());
        liveLecture.setLiveContent(liveLectureDto.getLiveContent());
        liveLecture.setStartDate(liveLectureDto.getStartDate());
        liveLecture.setEndDate(liveLectureDto.getEndDate());
        liveLecture.setStartTime(liveLectureDto.getStartTime());
        liveLecture.setEndTime(liveLectureDto.getEndTime());
        liveLecture.setMaxLiveNum(liveLectureDto.getMaxLiveNum());
        liveLecture.setRegDate(System.currentTimeMillis());
        liveLecture.setAvailableDay(liveLectureDto.getAvailableDay());

//        Optional<Users> userOptional = usersRepository.findById(liveLectureDto.getUserId());
//        if (userOptional.isPresent()) {
//            liveLecture.setUser(userOptional.get());
//        } else {
//            // 사용자 정보가 없을 경우 처리 로직 추가 필요
//            return null;
//        }
//
//        return liveLecturesRepository.save(liveLecture);
//    }


        if (liveLectureDto.getUserId() != null) {
            Optional<Users> userOptional = usersRepository.findById(liveLectureDto.getUserId());
            if (userOptional.isPresent()) {
                Users user = userOptional.get();
                liveLecture.setUser(user);

                LiveLectures savedLiveLecture = liveLecturesRepository.save(liveLecture);

                MyLiveLecture myLiveLecture = new MyLiveLecture();
                myLiveLecture.setLiveLecture(savedLiveLecture);
                myLiveLecture.setUser(user);
                myLiveLectureRepository.save(myLiveLecture);

                return savedLiveLecture;
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
    public List<MyLiveLecture> getMyLiveLecturesByUserId(Long userId) {
        return myLiveLectureRepository.findByUserId(userId);  // 특정 사용자가 등록한 화상 강의 목록을 조회
    }
}