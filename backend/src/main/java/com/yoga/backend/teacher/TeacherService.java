package com.yoga.backend.teacher;

import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;

import java.util.List;

/**
 * 강사 서비스 인터페이스
 */
public interface TeacherService {

    /**
     * 모든 강사 정보를 조회합니다.
     *
     * @param filter 필터 조건
     * @return 강사 정보 리스트
     */
    List<TeacherDto> getAllTeachers(TeacherFilter filter);

    /**
     * ID로 특정 강사 정보를 조회합니다.
     *
     * @param id 강사 ID
     * @return 강사 정보
     */
    DetailedTeacherDto getTeacherById(int id);
}
