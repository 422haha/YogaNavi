package com.yoga.backend.teacher.service;

import com.yoga.backend.teacher.TeacherFilter;
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
     * @param userId 사용자 ID
     * @return 강사 정보 리스트
     */
    List<TeacherDto> getAllTeachers(TeacherFilter filter, int userId);

    /**
     * 정렬된 강사 정보를 조회합니다.
     *
     * @param sorting 정렬 기준 (0: 최신순, 1: 인기순)
     * @param userId  사용자 ID
     * @param keyword 검색 키워드
     * @return 정렬된 강사 정보 리스트
     */
    List<TeacherDto> getSortedTeachers(int sorting, int userId, String keyword);

    /**
     * ID로 특정 강사 정보를 조회합니다.
     *
     * @param id     강사 ID
     * @param userId 사용자 ID
     * @return 강사 정보
     */
    DetailedTeacherDto getTeacherById(int id, int userId);

    /**
     * 강사 좋아요 상태를 토글합니다.
     *
     * @param teacherId 강사 ID
     * @param userId    사용자 ID
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    boolean toggleLike(int teacherId, int userId);

    /**
     * 검색 조건에 맞는 강사 정보를 조회합니다.
     *
     * @param filter  필터 조건
     * @param userId  사용자 ID
     * @param keyword 검색 키워드
     * @return 강사 정보 리스트
     */
    List<TeacherDto> searchTeachers(TeacherFilter filter, int userId, String keyword);

    /**
     * 해시태그로 강사 정보를 조회합니다.
     *
     * @param hashtag 해시태그
     * @param userId  사용자 ID
     * @return 강사 정보 리스트
     */
    List<TeacherDto> searchTeachersByHashtag(String hashtag, int userId);
}
