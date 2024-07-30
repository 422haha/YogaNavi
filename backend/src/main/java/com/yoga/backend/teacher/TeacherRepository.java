package com.yoga.backend.teacher;

import com.yoga.backend.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
