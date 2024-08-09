package com.yoga.backend.livelectures.repository;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 나의 실시간 강의 리포지토리 인터페이스
 */
public interface MyLiveLectureRepository extends JpaRepository<MyLiveLecture, Long> {

    /**
     * 특정 사용자의 예약 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자의 예약 목록
     */
    @Query("SELECT mll FROM MyLiveLecture mll JOIN FETCH mll.liveLecture WHERE mll.user.id = :userId")
    List<MyLiveLecture> findByUserId(@Param("userId") int userId);

    /**
     * 특정 실시간 강의의 예약 목록 조회
     *
     * @param liveId 실시간 강의 ID
     * @return 실시간 강의의 예약 목록
     */
    @Query("SELECT mll FROM MyLiveLecture mll JOIN FETCH mll.liveLecture WHERE mll.liveLecture.liveId = :liveId")
    List<MyLiveLecture> findByLiveLecture_LiveId(@Param("liveId") Long liveId);

    /**
     * 특정 강의와 사용자에 대한 예약 목록 조회
     *
     * @param liveId 실시간 강의 ID
     * @return 강의와 사용자에 대한 예약 목록
     */
    @Query("SELECT m FROM MyLiveLecture m JOIN FETCH m.user WHERE m.liveLecture.liveId = :liveId")
    List<MyLiveLecture> findByLiveLectureWithUser(@Param("liveId") Long liveId);

    /**
     * 특정 강의 ID에 대한 사용자와의 예약 목록 조회
     *
     * @param liveId 실시간 강의 ID
     * @return 강의 ID에 대한 사용자와의 예약 목록
     */
    @Query("SELECT mll FROM MyLiveLecture mll JOIN FETCH mll.user WHERE mll.liveLecture.liveId = :liveId")
    List<MyLiveLecture> findByLiveLectureIdWithUser(@Param("liveId") Long liveId);

    /**
     * 특정 강의의 현재 참여자 수 조회
     *
     * @param liveLecture 강의 엔티티
     * @param currentDate 현재 날짜와 시간
     * @return 현재 참여자 수
     */
    @Query("SELECT COUNT(mll) FROM MyLiveLecture mll WHERE mll.liveLecture = :liveLecture AND mll.endDate > :currentDate")
    int countByLiveLectureAndEndDateAfter(@Param("liveLecture") LiveLectures liveLecture,
        @Param("currentDate") Instant currentDate);

    //학생에게 fcm 전송을 위한 쿼리
    @Query("SELECT ml FROM MyLiveLecture ml JOIN FETCH ml.user JOIN FETCH ml.liveLecture l " +
        "WHERE l.liveId = :liveId " +
        "AND DATE(:currentDate) BETWEEN DATE(ml.startDate) AND DATE(ml.endDate) " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<MyLiveLecture> findParticipantsForTodayLecture(
        @Param("liveId") Long liveId,
        @Param("currentDate") LocalDate currentDate,
        @Param("dayOfWeek") String dayOfWeek
    );


    // home을 위한 쿼리
    @Query("SELECT ml FROM MyLiveLecture ml JOIN FETCH ml.liveLecture l " +
        "WHERE ml.user.id = :userId " +
        "AND (DATE(ml.startDate) <= DATE(:currentDate) " +
        "     AND DATE(ml.endDate) >= DATE(:currentDate) " +
        "     OR DATE(ml.endDate) > DATE(:currentDate)) " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<MyLiveLecture> findCurrentLecturesByUserId(
        @Param("userId") int userId,
        @Param("currentDate") LocalDate currentDate,
        @Param("dayOfWeek") String dayOfWeek
    );

    // history를 위한 쿼리
    @Query("SELECT ml FROM MyLiveLecture ml JOIN FETCH ml.liveLecture l " +
        "WHERE ml.user.id = :userId " +
        "AND (DATE(ml.startDate) <= DATE(:currentDate) " +
        "     AND DATE(ml.endDate) >= DATE(:currentDate) " +
        "     OR DATE(ml.endDate) < DATE(:currentDate)) " +
        "AND l.availableDay LIKE %:dayOfWeek%")
    List<MyLiveLecture> findPastAndOngoingLecturesByUserId(
        @Param("userId") int userId,
        @Param("currentDate") LocalDate currentDate,
        @Param("dayOfWeek") String dayOfWeek
    );
}
