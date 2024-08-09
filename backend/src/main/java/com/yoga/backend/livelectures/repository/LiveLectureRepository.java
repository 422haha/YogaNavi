package com.yoga.backend.livelectures.repository;

import com.yoga.backend.common.entity.LiveLectures;
import java.time.Instant;
import java.time.LocalDate;
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

    // fcm을 위한 쿼리. 전날 밤에 다음날 강의할 강의들을 캐시
    @Query("SELECT l FROM LiveLectures l WHERE " +
        "DATE(l.startDate) <= DATE(:tomorrow) AND " +
        "DATE(l.endDate) >= DATE(:tomorrow) AND " +
        "l.availableDay LIKE %:dayOfWeek% " +
        "ORDER BY l.startTime")
    List<LiveLectures> findTomorrowLectures(
        @Param("tomorrow") LocalDate tomorrow,
        @Param("dayOfWeek") String dayOfWeek
    );
    //fcm을 위한 쿼리. 캐시가 비어있을 시 실행
    @Query("SELECT l FROM LiveLectures l WHERE " +
        "DATE(l.startDate) <= DATE(:today) AND DATE(l.endDate) >= DATE(:today) " +
        "AND l.availableDay LIKE %:dayOfWeek% " +
        "ORDER BY l.startTime")
    List<LiveLectures> findLecturesForToday(
        @Param("today") LocalDate today,
        @Param("dayOfWeek") String dayOfWeek
    );

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

    //home을 위한 쿼리
    @Query("SELECT l FROM LiveLectures l WHERE l.user.id = :userId " +
        "AND (DATE(l.startDate) <= DATE(:currentDate) " +
        "     AND DATE(l.endDate) >= DATE(:currentDate) " +
        "     OR DATE(l.endDate) > DATE(:currentDate)) " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<LiveLectures> findLecturesByUserAndDateRange(
        @Param("userId") int userId,
        @Param("currentDate") LocalDate currentDate,
        @Param("dayOfWeek") String dayOfWeek
    );

    // history를 위한 쿼리
    @Query("SELECT l FROM LiveLectures l WHERE l.user.id = :userId " +
        "AND (DATE(l.startDate) <= DATE(:currentDate) " +
        "     AND DATE(l.endDate) >= DATE(:currentDate) " +
        "     OR DATE(l.endDate) < DATE(:currentDate)) " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<LiveLectures> findPastAndOngoingLecturesByUser(
        @Param("userId") int userId,
        @Param("currentDate") LocalDate currentDate,
        @Param("dayOfWeek") String dayOfWeek
    );

}
