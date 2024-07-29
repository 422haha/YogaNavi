package com.yoga.backend.teacher;

import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
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

    /**
     * 모든 강사 목록을 가져옵니다.
     *
     * @return 강사 목록
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeachers() {
        try {
            List<TeacherDto> teachers = teacherService.getAllTeachers();
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
     * @return 강사 상세 정보
     */
    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherDetail(@PathVariable int teacherId) {
        try {
            DetailedTeacherDto teacher = teacherService.getTeacherById(teacherId);
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
}
