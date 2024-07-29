package com.yoga.backend.teacher;

import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;

import java.util.List;

/**
 * 강사 서비스 인터페이스
 */
public interface TeacherService {
    /**
     * 모든 강사를 조회합니다.
     *
     * @return 강사 목록
     */
    List<TeacherDto> getAllTeachers();

    /**
     * 강사 ID로 강사를 조회합니다.
     *
     * @param id 강사 ID
     * @return 상세 강사 DTO
     */
    DetailedTeacherDto getTeacherById(int id);
}
