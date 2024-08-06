package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 실시간 강의 리포지토리 인터페이스
 */
public interface LiveLectureRepository extends JpaRepository<LiveLectures, Long> {

    @Query("SELECT ll FROM LiveLectures ll JOIN FETCH ll.user WHERE ll.user.id = :id")
    List<LiveLectures> findByUserId(@Param("id") int id);

    @Query("SELECT l FROM LiveLectures l WHERE DATE(l.startDate) = DATE(:startOfDay) AND FUNCTION('TIME', l.startTime) >= FUNCTION('TIME', :startOfDay) AND FUNCTION('TIME', l.startTime) < FUNCTION('TIME', :endOfDay) AND l.availableDay LIKE %:dayAbbreviation%")
    List<LiveLectures> findLecturesForToday(@Param("startOfDay") Instant startOfDay,
        @Param("endOfDay") Instant endOfDay, @Param("dayAbbreviation") String dayAbbreviation);

    @Query("SELECT ll FROM LiveLectures ll WHERE ll.maxLiveNum = :maxLiveNum AND ll.endDate > :currentDate")
    List<LiveLectures> findAllByMaxLiveNumAndEndDateAfter(@Param("maxLiveNum") int maxLiveNum,
        @Param("currentDate") Instant currentDate);

    @Query("SELECT ll FROM LiveLectures ll WHERE ll.maxLiveNum > :maxLiveNum AND ll.endDate > :currentDate")
    List<LiveLectures> findAllByMaxLiveNumGreaterThanAndEndDateAfter(
        @Param("maxLiveNum") int maxLiveNum, @Param("currentDate") Instant currentDate);

    @Query("SELECT ll FROM LiveLectures ll WHERE ll.user.id = :userId AND ll.maxLiveNum = :maxLiveNum AND ll.endDate > :now")
    List<LiveLectures> findByUserIdAndMaxLiveNumAndEndDateAfter(@Param("userId") int userId,
        @Param("maxLiveNum") int maxLiveNum, @Param("now") Instant now);

    @Query("SELECT ll FROM LiveLectures ll WHERE ll.user.id = :userId AND ll.maxLiveNum > :maxLiveNum AND ll.endDate > :now")
    List<LiveLectures> findByUserIdAndMaxLiveNumGreaterThanAndEndDateAfter(
        @Param("userId") int userId,
        @Param("maxLiveNum") int maxLiveNum, @Param("now") Instant now);

    @Query("SELECT l FROM LiveLectures l WHERE l.user.id = :userId " +
        "AND l.endDate >= :currentDate " +
        "AND l.startDate <= :endDate " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<LiveLectures> findLecturesByUserAndDateRange(
        @Param("userId") int userId,
        @Param("currentDate") Instant currentDate,
        @Param("endDate") Instant endDate,
        @Param("dayOfWeek") String dayOfWeek
    );
}
