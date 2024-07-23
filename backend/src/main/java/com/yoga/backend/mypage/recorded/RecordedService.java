package com.yoga.backend.mypage.recorded;


import com.yoga.backend.mypage.recorded.dto.LectureCreationStatus;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecordedService {

    List<LectureDto> getMyLectures(int userId);

    List<LectureDto> getLikeLectures(int userId);

    void saveLecture(LectureDto lectureDto);

    LectureDto getLectureDetails(Long recordedId, int userId);

    LectureDto updateLecture(Long lectureId, LectureDto lectureDto, int userId);

    void deleteLecture(Long lectureId, int userId);

    LectureDto setLike(Long recordedId, int userId);

    LectureDto setDislike(Long recordedId, int userId);
}