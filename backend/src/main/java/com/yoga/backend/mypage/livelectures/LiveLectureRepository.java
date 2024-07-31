package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LiveLectureRepository extends JpaRepository<LiveLectures, Long> {
    
    @Query("SELECT ll FROM LiveLectures ll JOIN FETCH ll.user WHERE ll.user.id = :id")
    List<LiveLectures> findByUserId(int id);

    List<LiveLectures> findByStartTimeBetween(Instant start, Instant end);

    @Query("SELECT l FROM LiveLectures l WHERE DATE(l.startDate) = DATE(:startOfDay) AND FUNCTION('TIME', l.startTime) >= FUNCTION('TIME', :startOfDay) AND FUNCTION('TIME', l.startTime) < FUNCTION('TIME', :endOfDay) AND l.availableDay LIKE %:dayAbbreviation%")
    List<LiveLectures> findLecturesForToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("dayAbbreviation") String dayAbbreviation);

}