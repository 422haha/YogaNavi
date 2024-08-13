package com.yoga.backend.teacher.controller;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.teacher.TeacherFilter;
import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import com.yoga.backend.teacher.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 강사 컨트롤러
 */
@RestController
@RequestMapping("")
public class TeacherController {

    private final TeacherService teacherService;
    private final JwtUtil jwtUtil;

    public TeacherController(TeacherService teacherService, JwtUtil jwtUtil) {
        this.teacherService = teacherService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 모든 강사 목록 조회
     *
     * @return 강사 목록
     */
    @GetMapping("/teacher")
    public ResponseEntity<Map<String, Object>> getAllTeachers(
        @RequestParam(value = "sorting", defaultValue = "0") int sorting, // 정렬 기준
        @RequestParam(value = "startTime", defaultValue = "0") long startTime, // 강의 시작 시간
        @RequestParam(value = "endTime", defaultValue = "86340000") long endTime, // 강의 종료 시간
        @RequestParam(value = "day", defaultValue = "MON,TUE,WED,THU,FRI,SAT,SUN,") String day,
        // 강의 요일
        @RequestParam(value = "period", defaultValue = "3") int period, // 필터 기간
        @RequestParam(value = "maxLiveNum", defaultValue = "2") int maxLiveNum, // 최대 수강자 수
        @RequestParam(value = "searchKeyword", defaultValue = "") String searchKeyword, // 검색 키워드
        @RequestHeader("Authorization") String token) { // 인증 토큰

        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        // 필터 설정
        TeacherFilter filter = new TeacherFilter();
        filter.setStartTime(Instant.ofEpochMilli(startTime).toEpochMilli());
        filter.setEndTime(Instant.ofEpochMilli(endTime).toEpochMilli());
        filter.setDay(day);
        filter.setPeriod(period);
        filter.setMaxLiveNum(maxLiveNum);
        filter.setSearchKeyword(searchKeyword);

        try {
            List<TeacherDto> teachers = teacherService.getAllTeachers(filter, sorting, userId);

            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("data", teachers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 응답 생성
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 정렬된 강사 목록 조회
     *
     * @param sorting 정렬 방식 (0: 최신순, 1: 인기순)
     * @param token   인증 토큰
     * @return 정렬된 강사 목록
     */
    @GetMapping("/teacher/sort/{sorting}")
    public ResponseEntity<Map<String, Object>> getSortedTeachers(
        @PathVariable int sorting, // 정렬 기준
        @RequestParam(value = "searchKeyword", defaultValue = "") String searchKeyword, // 검색 키워드
        @RequestHeader("Authorization") String token) { // 인증 토큰

        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        try {
            List<TeacherDto> teachers = teacherService.getSortedTeachers(sorting, userId,
                searchKeyword);
            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("data", teachers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 응답 생성
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 강사 상세 정보 조회
     *
     * @param teacherId 강사 ID
     * @param token     인증 토큰
     * @return 강사 상세 정보
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherDetail(@PathVariable int teacherId,
        // 강사 ID
        @RequestHeader("Authorization") String token) { // 인증 토큰
        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        try {
            DetailedTeacherDto teacher = teacherService.getTeacherById(teacherId, userId);
            // 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("data", teacher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 오류 응답 생성
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // 오류 응답 생성
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 강사 좋아요/좋아요 취소
     *
     * @param teacherId 강사 ID
     * @param token     인증 토큰
     * @return 좋아요 상태
     */
    @PostMapping("/teacher/like/{teacherId}")
    public ResponseEntity<Map<String, Object>> likeTeacher(@PathVariable int teacherId, // 강사 ID
        @RequestHeader("Authorization") String token) { // 인증 토큰
        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isLiked = teacherService.toggleLike(teacherId, userId);
            // 응답 생성
            response.put("message", isLiked ? "좋아요 성공" : "좋아요 취소");
            response.put("data", true);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // 오류 응답 생성
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/mypage/like-teacher")
    public ResponseEntity<Map<String, Object>> getLikeTeachers(
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        Map<String, Object> response = new HashMap<>();
        try {
            List<TeacherDto> likeTeachers = teacherService.getLikeTeachers(userId);
            // 응답 생성
            response.put("message", "success");
            response.put("data", likeTeachers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 응답 생성
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
