package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 실시간 강의 리포지토리 인터페이스
 * 데이터베이스와의 상호작용을 정의
 */
public interface LiveLectureRepository extends JpaRepository<LiveLectures, Integer> { // Long
    /**
     * 사용자 ID로 화상 강의를 조회하는 메서드.
     *
     * @param id 사용자 ID
     * @return 사용자 ID에 해당하는 화상 강의 목록
     */
    @Query("SELECT ll FROM LiveLectures ll JOIN FETCH ll.user WHERE ll.user.id = :id")
    List<LiveLectures> findByUserId(Integer id);

}