package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import java.util.List;
/**
 * 실시간 강의 서비스 인터페이스
 * 강의 생성 및 조회 등의 비즈니스 로직을 정의
 */
public interface LiveLectureService {
    /**
     * 실시간 강의를 생성
     *
     * @param liveLectureCreateDto 실시간 강의 DTO
     * @return 생성된 실시간 강의 엔티티
     */
    LiveLectureCreateResponseDto createLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    //    @Override
//    public LiveLectures createLiveLecture(LiveLectureCreateDto liveLectureCreateDto) {
//        LiveLectures liveLecture = new LiveLectures();
//        liveLecture.setLiveTitle(liveLectureCreateDto.getLiveTitle());
//        liveLecture.setLiveContent(liveLectureCreateDto.getLiveContent());
//        liveLecture.setStartDate(liveLectureCreateDto.getStartDate());
//        liveLecture.setEndDate(liveLectureCreateDto.getEndDate());
//        liveLecture.setStartTime(liveLectureCreateDto.getStartTime());
//        liveLecture.setEndTime(liveLectureCreateDto.getEndTime());
//        liveLecture.setMaxLiveNum(liveLectureCreateDto.getMaxLiveNum());
//        liveLecture.setRegDate(System.currentTimeMillis());
//        liveLecture.setAvailableDay(liveLectureCreateDto.getAvailableDay());
//
//        if (liveLectureCreateDto.getUserId() != null) {
//            Optional<Users> userOptional = usersRepository.findById(liveLectureCreateDto.getUserId());
//            if (userOptional.isPresent()) {
//                Users user = userOptional.get();
//                liveLecture.setUser(user);
//
//                LiveLectures savedLiveLecture = liveLecturesRepository.save(liveLecture);
//
//                MyLiveLecture myLiveLecture = new MyLiveLecture();
//                myLiveLecture.setLiveLecture(savedLiveLecture);
//                myLiveLecture.setUser(user);
//                myLiveLectureRepository.save(myLiveLecture);
//
//                return savedLiveLecture;
//            } else {
//                // 사용자 정보가 없을 경우 처리 로직 추가 필요
//                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
//            }
//        } else {
//            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다");
//        }
//    }

//    LiveLectures createLiveLecture(LiveLectureCreateDto liveLectureCreateDto,
//        HttpServletRequest request);

    /**
     * 모든 실시간 강의를 조회
     * @return 모든 실시간 강의 리스트
     */
    List<LiveLectures> getAllLiveLectures();
//    List<MyLiveLecture> getMyLiveLecturesByUserId(int userId);
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> getMyLiveLecturesByUserId(Integer userId);// 특정 사용자가 등록한 화상 강의 목록을 조회하는 메서드
    List<LiveLectures> getLiveLecturesByUserId(Integer userId); // 사용자 ID로 화상 강의를 조회하는 메서드 추가

    //화상 강의 수정
    LiveLectures updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    LiveLectures getLiveLectureById(Integer liveId); // 단일 화상강의 조회 메서드
//    LiveLectures getLiveLectureByIdAndUserId(Integer liveId, Integer userId); // 단일 화상강의 조회 메서드 +신원확인

    boolean isLectureOwner(Integer liveId, Integer userId);

    void deleteLiveLectureById(Integer liveId);

//    void deleteMyLiveLectureByLiveId(Integer liveId);

}
