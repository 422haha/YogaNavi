package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface LiveLectureRepository extends JpaRepository<LiveLectures, Long> {
    
    @Query("SELECT ll FROM LiveLectures ll JOIN FETCH ll.user WHERE ll.user.id = :id")
    List<LiveLectures> findByUserId(int id);
}