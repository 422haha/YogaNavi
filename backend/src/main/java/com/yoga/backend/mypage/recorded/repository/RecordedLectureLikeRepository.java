package com.yoga.backend.mypage.recorded.repository;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedLectureLikeRepository extends JpaRepository<RecordedLectureLike, Long> {

    boolean existsByLectureIdAndUserEmail(Long lectureId, String userEmail);

    int countByLectureId(Long lectureId);

    void deleteByLectureIdAndUserEmail(Long lectureId, String userEmail);
}