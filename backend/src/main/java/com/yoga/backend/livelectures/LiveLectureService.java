package com.yoga.backend.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.livelectures.dto.LiveLectureCreateResponseDto;
import java.util.List;

/**
 * 실시간 강의 서비스 인터페이스
 * 강의 생성 및 조회 등의 비즈니스 로직을 정의
 */
public interface LiveLectureService {
    LiveLectureCreateResponseDto createLiveLecture(LiveLectureCreateDto liveLectureCreateDto);
    List<LiveLectures> getAllLiveLectures();
    List<MyLiveLecture> getMyLiveLecturesByUserId(int userId);
    List<LiveLectures> getLiveLecturesByUserId(int userId);
    LiveLectures updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto);
    LiveLectures getLiveLectureById(Long liveId);
    boolean isLectureOwner(Long liveId, int userId);
    void deleteLiveLectureById(Long liveId);
//    CompletableFuture<Void> updateIsOnAir(Long liveId, boolean isOnAir); // 비동기 처리
    void updateIsOnAir(Long liveId, boolean isOnAir);

}