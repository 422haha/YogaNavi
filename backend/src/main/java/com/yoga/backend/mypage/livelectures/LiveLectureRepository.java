package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LiveLectureRepository extends JpaRepository<LiveLectures, Long> {
    List<LiveLectures> findByUserId(int id);
}