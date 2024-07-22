package com.yoga.backend.mypage.recorded.repository;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedLectureLikeRepository extends JpaRepository<RecordedLectureLike, Long> {
    boolean existsByLectureIdAndUserId(Long lectureId, int userId);
    int countByLectureId(Long lectureId);
    void deleteByLectureIdAndUserId(Long lectureId, int userId);
}