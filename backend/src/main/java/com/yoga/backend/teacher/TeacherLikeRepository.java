package com.yoga.backend.teacher;

import com.yoga.backend.common.entity.TeacherLike;
import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 강사 좋아요 리포지토리 인터페이스
 */
public interface TeacherLikeRepository extends JpaRepository<TeacherLike, Long> {

    /**
     * 특정 사용자와 강사에 대한 좋아요 정보를 조회합니다.
     *
     * @param teacher 강사
     * @param user    사용자
     * @return TeacherLike 객체
     */
    TeacherLike findByTeacherAndUser(Users teacher, Users user);
}
