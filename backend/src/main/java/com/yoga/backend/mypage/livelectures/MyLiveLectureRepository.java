package com.yoga.backend.mypage.livelectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 나의 실시간 강의 리포지토리 인터페이스 데이터베이스와의 상호작용을 정의
 */
public interface MyLiveLectureRepository extends JpaRepository<MyLiveLecture, Integer> {
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> findByUserId(Integer userId);
    /**
     * 특정 화상 강의 ID에 대한 나의 실시간 강의 목록을 조회
     *
     * @param liveId 화상 강의 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> findByLiveLecture_LiveId(Integer liveId);
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회
     *
     * @param userId 사용자 ID
     * @return 나의 실시간 강의 리스트
     */
    List<MyLiveLecture> findByUserId(int userId);
}