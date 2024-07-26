package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateResponseDto;
import java.util.List;
/**
 * 실시간 강의 서비스 인터페이스
 * 강의 생성 및 조회 등의 비즈니스 로직을 정의
 */
public interface LiveLectureService {

    /**
     * 실시간 강의를 생성.
     *
     * @param liveLectureCreateDto 실시간 강의 DTO
     * @return 생성된 실시간 강의 응답 DTO
     */
    LiveLectureCreateResponseDto createLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    /**
     * 모든 실시간 강의를 조회
     * @return 모든 실시간 강의 리스트
     */
    List<LiveLectures> getAllLiveLectures();

    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회.
     *
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> getMyLiveLecturesByUserId(Integer userId);

    // 사용자 ID로 화상 강의를 조회하는 메서드 추가
    /**
     * 사용자 ID로 화상 강의를 조회.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 실시간 강의 리스트
     */
    List<LiveLectures> getLiveLecturesByUserId(Integer userId);

    // 화상 강의 수정
    /**
     * 화상 강의를 수정.
     *
     * @param liveLectureCreateDto 수정할 화상 강의 DTO
     * @return 수정된 화상 강의 엔티티
     */
    LiveLectures updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    // 단일 화상강의 조회 메서드
    /**
     * 단일 화상 강의를 조회.
     *
     * @param liveId 화상 강의 ID
     * @return 해당 화상 강의 엔티티
     */
    LiveLectures getLiveLectureById(Integer liveId);

    // 화상 강의 소유자 확인 메서드
    /**
     * 화상 강의의 소유자인지 확인.
     *
     * @param liveId 화상 강의 ID
     * @param userId 사용자 ID
     * @return 소유자 여부
     */
    boolean isLectureOwner(Integer liveId, Integer userId);

    // 화상 강의 삭제
    /**
     * 화상 강의를 삭제.
     *
     * @param liveId 화상 강의 ID
     */
    void deleteLiveLectureById(Integer liveId);

}
