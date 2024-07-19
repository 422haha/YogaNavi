package com.yoga.backend.mypage.recorded;


import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.List;

public interface RecordedService {

    List<LectureDto> getMyLectures(String email);

    LectureDto saveLecture(LectureDto lectureDto);

    LectureDto getLectureDetails(Long recordedId, String email);
}

