package com.yoga.backend.mypage.recorded;


import com.yoga.backend.mypage.recorded.dto.LectureCreationStatus;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecordedService {

    List<LectureDto> getMyLectures(String email);

    List<LectureDto> getLikeLectures(String email);

    CompletableFuture<LectureDto> saveLectureAsync(LectureDto lectureDto, String sessionId);

    LectureDto saveLecture(LectureDto lectureDto);

    LectureCreationStatus getLectureCreationStatus(String sessionId);

    LectureDto getLectureDetails(Long recordedId, String email);

    LectureDto updateLecture(Long lectureId, LectureDto lectureDto, String email);

    void deleteLecture(Long lectureId, String email);

    LectureDto setLike(Long recordedId, String email);

    LectureDto setDislike(Long recordedId, String email);
}

