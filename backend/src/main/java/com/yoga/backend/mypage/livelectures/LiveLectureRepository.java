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
    List<LiveLectures> findByUserId(int id);

    List<LiveLectures> findByStartTimeBetween(Instant start, Instant end);

    @Query("SELECT l FROM LiveLectures l WHERE DATE(l.startDate) = DATE(:startOfDay) AND FUNCTION('TIME', l.startTime) >= FUNCTION('TIME', :startOfDay) AND FUNCTION('TIME', l.startTime) < FUNCTION('TIME', :endOfDay) AND l.availableDay LIKE %:dayAbbreviation%")
    List<LiveLectures> findLecturesForToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("dayAbbreviation") String dayAbbreviation);

    /**
     * 최대 수강자 수가 1인 실시간 강의를 조회합니다. (1대1 수업)
     *
     * @param maxLiveNum 최대 수강자 수
     * @return 실시간 강의 목록
     */
    List<LiveLectures> findAllByMaxLiveNum(int maxLiveNum);

    /**
     * 최대 수강자 수가 1보다 큰 실시간 강의를 조회합니다. (1대다 수업)
     *
     * @param maxLiveNum 기준 최대 수강자 수
     * @return 실시간 강의 목록
     */
    List<LiveLectures> findAllByMaxLiveNumGreaterThan(int maxLiveNum);
}
