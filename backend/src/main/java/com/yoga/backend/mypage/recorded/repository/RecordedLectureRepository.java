package com.yoga.backend.mypage.recorded.repository;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedLectureRepository extends JpaRepository<RecordedLecture, Long> {
    List<RecordedLecture> findByUserId(int userId);
}