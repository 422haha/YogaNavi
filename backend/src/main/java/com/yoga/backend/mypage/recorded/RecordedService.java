package com.yoga.backend.mypage.recorded;


import com.yoga.backend.mypage.recorded.dto.DeleteDto;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.List;

public interface RecordedService {

    List<LectureDto> getMyLectures(int userId);

    List<LectureDto> getLikeLectures(int userId);

    void saveLecture(LectureDto lectureDto);

    LectureDto getLectureDetails(Long recordedId, int userId);

    boolean updateLecture(LectureDto lectureDto);

    void deleteLectures(DeleteDto deleteDto, int userId);

    boolean toggleLike(Long recordedId, int userId);

    List<LectureDto> getAllLectures(int userId, int page, int size, String sort);

    List<LectureDto> searchLectures(int userId, String keyword, String sort, int page, int size, boolean title, boolean content);
}