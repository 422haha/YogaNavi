package com.yoga.backend.teacher;

import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 강사 리포지토리 인터페이스
 */
@Repository
public interface TeacherRepository extends JpaRepository<Users, Integer> {
    // 필요한 추가 메서드 정의 가능
}
