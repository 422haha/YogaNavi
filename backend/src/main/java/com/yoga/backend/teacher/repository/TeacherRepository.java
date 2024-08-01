package com.yoga.backend.teacher.repository;

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
     * @param startTime        강의 시작 시간 (밀리초 단위 타임스탬프)
     * @param endTime          강의 종료 시간 (밀리초 단위 타임스탬프)
     * @param days             강의 요일 (쉼표로 구분된 문자열)
     * @param period           필터 기간 (0: 일주일 후, 1: 한 달 후, 2: 세 달 후, 3: 전체 기간)
     * @param weekAfter        일주일 후 (밀리초 단위 타임스탬프)
     * @param monthAfter       한 달 후 (밀리초 단위 타임스탬프)
     * @param threeMonthsAfter 세 달 후 (밀리초 단위 타임스탬프)
     * @param maxLiveNum       최대 수강자 수 (1: 1대1, 1보다 큰 값: 1대다)
     * @return 필터 조건에 맞는 강사 리스트
     */
    @Query(value =
        "SELECT DISTINCT u.* FROM users u " +
            "JOIN live_lectures l ON u.user_id = l.user_id " +
            "WHERE l.start_time >= FROM_UNIXTIME(:startTime / 1000) " +
            "AND l.end_time <= FROM_UNIXTIME(:endTime / 1000) " +
            "AND (" +
            "  (:days = 'MON,TUE,WED,THU,FRI,SAT,SUN,' " +
            "   OR (INSTR(:days, 'MON,') > 0 AND l.available_day LIKE '%MON%') OR " +
            "      (INSTR(:days, 'TUE,') > 0 AND l.available_day LIKE '%TUE%') OR " +
            "      (INSTR(:days, 'WED,') > 0 AND l.available_day LIKE '%WED%') OR " +
            "      (INSTR(:days, 'THU,') > 0 AND l.available_day LIKE '%THU%') OR " +
            "      (INSTR(:days, 'FRI,') > 0 AND l.available_day LIKE '%FRI%') OR " +
            "      (INSTR(:days, 'SAT,') > 0 AND l.available_day LIKE '%SAT%') OR " +
            "      (INSTR(:days, 'SUN,') > 0 AND l.available_day LIKE '%SUN%')) " +
            ") " +
            "AND (" +
            "  (:period = 0 AND l.end_date > FROM_UNIXTIME(:weekAfter / 1000)) OR " +
            "  (:period = 1 AND l.end_date > FROM_UNIXTIME(:monthAfter / 1000)) OR " +
            "  (:period = 2 AND l.end_date > FROM_UNIXTIME(:threeMonthsAfter / 1000)) OR " +
            "  (:period = 3) " +
            ") " +
            "AND (" +
            "  (:maxLiveNum = 1 AND l.max_live_num > 0) OR " +
            "  (:maxLiveNum != 1 AND l.max_live_num = 1) " +
            ")",
        nativeQuery = true)
    List<Users> findTeachersByLectureFilter(
        @Param("startTime") long startTime,
        @Param("endTime") long endTime,
        @Param("days") String days,
        @Param("period") int period,
        @Param("weekAfter") long weekAfter,
        @Param("monthAfter") long monthAfter,
        @Param("threeMonthsAfter") long threeMonthsAfter,
        @Param("maxLiveNum") int maxLiveNum
    );
}