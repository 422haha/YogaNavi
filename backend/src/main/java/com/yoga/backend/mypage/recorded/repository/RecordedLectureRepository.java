package com.yoga.backend.mypage.recorded.repository;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedLectureRepository extends JpaRepository<RecordedLecture, Long> {
}
