package com.yoga.backend.mypage.recorded.repository;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureLike;
import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedLectureLikeRepository extends JpaRepository<RecordedLectureLike, Long> {

    boolean existsByLectureAndUser(RecordedLecture lecture, Users user);

    void deleteByLectureAndUser(RecordedLecture lecture, Users user);
}