package com.yoga.backend.teacher;

import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 강사 컨트롤러 클래스
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 모든 강사 목록을 가져옵니다.
     *
     * @return 강사 목록
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeachers(
        @RequestHeader(value = "sorting", defaultValue = "0") int sorting,
        @RequestHeader(value = "startTime", defaultValue = "0") long startTime,
        @RequestHeader(value = "endTime", defaultValue = "86400000") long endTime,
        @RequestHeader(value = "day", defaultValue = "MON, TUE, WED, THU, FRI, SAT, SUN") String day,
        @RequestHeader(value = "period", defaultValue = "3") int period,
        @RequestHeader(value = "maxLiveNum", defaultValue = "1") int maxLiveNum,
        @RequestHeader("Authorization") String token) {

        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        TeacherFilter filter = new TeacherFilter();
        filter.setSorting(sorting);
        filter.setStartTime(startTime);
        filter.setEndTime(endTime);
        filter.setDay(day);
        filter.setPeriod(period);
        filter.setMaxLiveNum(maxLiveNum);

        try {
            List<TeacherDto> teachers = teacherService.getAllTeachers(filter, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("data", teachers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 강사 상세 정보를 가져옵니다.
     *
     * @param teacherId 강사 ID
     * @param token     인증 토큰
     * @return 강사 상세 정보
     */
    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherDetail(@PathVariable int teacherId,
        @RequestHeader("Authorization") String token) {
        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        try {
            DetailedTeacherDto teacher = teacherService.getTeacherById(teacherId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "success");
            response.put("data", teacher);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
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
    @PostMapping("/like/{teacherId}")
    public ResponseEntity<Map<String, Object>> likeTeacher(@PathVariable int teacherId,
        @RequestHeader("Authorization") String token) {
        // 사용자 ID를 토큰에서 추출
        int userId = jwtUtil.getUserIdFromToken(token);

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isLiked = teacherService.toggleLike(teacherId, userId);
            response.put("message", isLiked ? "좋아요 성공" : "좋아요 취소");
            response.put("data", true);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
