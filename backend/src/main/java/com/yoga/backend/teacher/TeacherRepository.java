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
    @Query("SELECT DISTINCT u FROM Users u JOIN u.liveLectures l WHERE " +
        "l.startTime >= :startTime AND l.endTime <= :endTime AND " +
        "(:day = 'MON,TUE,WED,THU,FRI,SAT,SUN' OR FUNCTION('REGEXP_LIKE', l.availableDay, FUNCTION('REPLACE', :day, ',', '|'))) AND "
        +
        "((:period = 0 AND l.endDate > :weekAfter) OR " +
        "(:period = 1 AND l.endDate > :monthAfter) OR " +
        "(:period = 2 AND l.endDate > :threeMonthsAfter) OR " +
        "(:period = 3)) AND " +
        "(:maxLiveNum = 0 OR l.maxLiveNum = :maxLiveNum)")
    List<Users> findTeachersByLectureFilter(
        @Param("startTime") long startTime,
        @Param("endTime") long endTime,
        @Param("day") String day,
        @Param("period") int period,
        @Param("weekAfter") long weekAfter,
        @Param("monthAfter") long monthAfter,
        @Param("threeMonthsAfter") long threeMonthsAfter,
        @Param("maxLiveNum") int maxLiveNum);
}
