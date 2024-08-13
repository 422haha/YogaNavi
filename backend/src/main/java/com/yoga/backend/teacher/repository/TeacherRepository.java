package com.yoga.backend.teacher.repository;

import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * 강사 리포지토리
 */
public interface TeacherRepository extends JpaRepository<Users, Integer> {

    /**
     * 모든 강사 정보 조회
     *
     * @return 강사 리스트
     */
    @Query("SELECT u FROM Users u WHERE u.role = 'TEACHER' and u.isDeleted = false")
    List<Users> findAllTeachers();

    /**
     * 해시태그로 강사 정보 조회
     *
     * @param hashtag 해시태그
     * @return 강사 리스트
     */
    @Query("SELECT u FROM Users u JOIN u.hashtags h WHERE h.name = :hashtag AND u.role = 'TEACHER' and u.isDeleted =false")
    List<Users> findTeachersByHashtag(@Param("hashtag") String hashtag);

    /**
     * 필터 조건에 맞는 강사 정보 조회
     *
     * @param startTime  강의 시작 시간 (Instant)
     * @param endTime    강의 종료 시간 (Instant)
     * @param days       강의 요일 (쉼표로 구분된 문자열)
     * @param period     필터 기간 (0: 일주일 후, 1: 한 달 후, 2: 세 달 후, 3: 전체 기간)
     * @param maxLiveNum 최대 수강자 수 (0: 1대1, 1: 1대다, 2: 전체)
     * @return 필터 조건에 맞는 강사 리스트
     */
    @Query(value = "SELECT DISTINCT u.* FROM users u " +
        "JOIN live_lectures l ON u.user_id = l.user_id " +
        "WHERE u.is_deleted = false " +
        "AND l.start_time >= :startTime " +
        "AND l.end_time <= :endTime " +
        "AND l.end_date > CURRENT_TIMESTAMP " +
        "AND NOT EXISTS (" +
        "  SELECT 1 FROM (" +
        "    SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(l.available_day, ',', numbers.n), ',', -1) AS available_day "
        +
        "    FROM (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL " +
        "          SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL " +
        "          SELECT 9 UNION ALL SELECT 10) numbers " +
        "    WHERE numbers.n <= 1 + (LENGTH(l.available_day) - LENGTH(REPLACE(l.available_day, ',', ''))) "
        +
        "  ) AS lecture_days " +
        "  LEFT JOIN (" +
        "    SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(:days, ',', numbers.n), ',', -1) AS day " +
        "    FROM (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL " +
        "          SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL " +
        "          SELECT 9 UNION ALL SELECT 10) numbers " +
        "    WHERE numbers.n <= 1 + (LENGTH(:days) - LENGTH(REPLACE(:days, ',', ''))) " +
        "  ) AS selected_days ON lecture_days.available_day = selected_days.day " +
        "  WHERE selected_days.day IS NULL" +
        ") " +
        "AND (" +
        "  (:period = 0 AND l.end_date > DATE_ADD(NOW(), INTERVAL 1 WEEK)) OR " +
        "  (:period = 1 AND l.end_date > DATE_ADD(NOW(), INTERVAL 1 MONTH)) OR " +
        "  (:period = 2 AND l.end_date > DATE_ADD(NOW(), INTERVAL 3 MONTH)) OR " +
        "  (:period = 3) " +
        ") " +
        "AND (" +
        "  CASE WHEN :maxLiveNum = 0 THEN l.max_live_num = 1 " +
        "       WHEN :maxLiveNum = 1 THEN l.max_live_num > 1 " +
        "       ELSE l.max_live_num > 0 END" +
        ")" +
        "AND (" +
        "  (SELECT COUNT(*) FROM my_live_lecture mll WHERE mll.live_id = l.live_id AND mll.end_date > CURRENT_TIMESTAMP) < l.max_live_num "
        +
        ")", nativeQuery = true)
    List<Users> findTeachersByLectureFilter(
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("days") String days,
        @Param("period") int period,
        @Param("maxLiveNum") int maxLiveNum
    );
}
