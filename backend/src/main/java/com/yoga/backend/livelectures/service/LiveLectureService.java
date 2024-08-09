package com.yoga.backend.livelectures.service;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.livelectures.dto.LiveLectureCreateResponseDto;
import java.util.List;

public interface LiveLectureService {

    LiveLectureCreateResponseDto createLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    List<LiveLectures> getLiveLecturesByUserId(int userId);

    void updateLiveLecture(LiveLectureCreateDto liveLectureCreateDto);

    LiveLectures getLiveLectureById(Long liveId);

    boolean isLectureOwner(Long liveId, int userId);

    void deleteLiveLectureById(Long liveId);

    void updateIsOnAir(Long liveId, boolean isOnAir);

}