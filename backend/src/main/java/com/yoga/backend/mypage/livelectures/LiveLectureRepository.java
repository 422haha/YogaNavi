package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 실시간 강의 리포지토리 인터페이스
 * 데이터베이스와의 상호작용을 정의
 */
public interface LiveLectureRepository extends JpaRepository<LiveLectures, Long> {
    // JpaRepository를 상속받아 기본적인 CRUD 메서드들을 제공하는 인터페이스

}