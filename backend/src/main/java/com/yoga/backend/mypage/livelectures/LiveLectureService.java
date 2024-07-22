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
     * @param liveLectureDto 실시간 강의 DTO
     * @return 생성된 실시간 강의 엔티티
     */
    LiveLectures createLiveLecture(LiveLectureDto liveLectureDto);
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
    List<MyLiveLecture> getMyLiveLecturesByUserId(Long userId);// 특정 사용자가 등록한 화상 강의 목록을 조회하는 메서드
}
