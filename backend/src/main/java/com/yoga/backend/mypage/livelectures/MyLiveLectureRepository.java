package com.yoga.backend.mypage.livelectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 나의 실시간 강의 리포지토리 인터페이스 데이터베이스와의 상호작용을 정의
 */
public interface MyLiveLectureRepository extends JpaRepository<MyLiveLecture, Long> {
    @Query("SELECT mll FROM MyLiveLecture mll JOIN FETCH mll.liveLecture WHERE mll.user.id = :userId")
    List<MyLiveLecture> findByUserId(int userId);

    @Query("SELECT mll FROM MyLiveLecture mll JOIN FETCH mll.liveLecture WHERE mll.liveLecture.liveId = :liveId")
    List<MyLiveLecture> findByLiveLecture_LiveId(Long liveId);
}