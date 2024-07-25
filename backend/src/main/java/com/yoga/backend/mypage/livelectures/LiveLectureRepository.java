package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 실시간 강의 리포지토리 인터페이스
 * 데이터베이스와의 상호작용을 정의
 */
public interface LiveLectureRepository extends JpaRepository<LiveLectures, Integer> { // Long
    List<LiveLectures> findByUserId(Integer id); // 사용자 ID로 화상 강의를 조회하는 메서드 추가

}