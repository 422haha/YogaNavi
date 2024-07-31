package com.yoga.backend.teacher;

import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 강사 리포지토리
 */
public interface TeacherRepository extends JpaRepository<Users, Integer> {

    /**
     * 모든 강사 정보를 조회합니다.
     *
     * @return 강사 리스트
     */
    @Query("SELECT u FROM Users u WHERE u.role = 'TEACHER'")
    List<Users> findAllTeachers();

    /**
     * 해시태그로 강사 정보를 조회합니다.
     *
     * @param hashtag 해시태그
     * @return 강사 리스트
     */
    @Query("SELECT u FROM Users u JOIN u.hashtags h WHERE h.name = :hashtag AND u.role = 'TEACHER'")
    List<Users> findTeachersByHashtag(@Param("hashtag") String hashtag);

    /**
     * 필터 조건에 맞는 강사 정보를 조회합니다.
     *
     * @param startTime        강의 시작 시간
     * @param endTime          강의 종료 시간
     * @param day              강의 요일
     * @param period           필터 기간
     * @param weekAfter        일주일 후
     * @param monthAfter       한 달 후
     * @param threeMonthsAfter 세 달 후
     * @param maxLiveNum       최대 수강자 수
     * @return 필터 조건에 맞는 강사 리스트
     */
    @Query(value =
        "SELECT DISTINCT u.* FROM users u JOIN live_lectures l ON u.user_id = l.user_id WHERE " +
            "l.start_time >= :startTime AND l.end_time <= :endTime AND " +
            "(:day = 'MON,TUE,WED,THU,FRI,SAT,SUN' OR REGEXP_LIKE(l.available_day, REPLACE(:day, ',', '|'))) AND "
            +
            "((:period = 0 AND l.end_date > :weekAfter) OR " +
            "(:period = 1 AND l.end_date > :monthAfter) OR " +
            "(:period = 2 AND l.end_date > :threeMonthsAfter) OR " +
            "(:period = 3)) AND " +
            "(CASE WHEN :maxLiveNum = 1 THEN l.max_live_num > 0 ELSE l.max_live_num = 1 END)", nativeQuery = true)
    List<Users> findTeachersByLectureFilter(
        @Param("startTime") long startTime, // 강의 시작 시간
        @Param("endTime") long endTime, // 강의 종료 시간
        @Param("day") String day, // 강의 요일
        @Param("period") int period, // 필터 기간
        @Param("weekAfter") long weekAfter, // 일주일 후
        @Param("monthAfter") long monthAfter, // 한 달 후
        @Param("threeMonthsAfter") long threeMonthsAfter, // 세 달 후
        @Param("maxLiveNum") int maxLiveNum); // 최대 수강자 수
}
