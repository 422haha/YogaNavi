package com.yoga.backend.teacher.controller;

import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.teacher.dto.ReservationRequestDto;
import com.yoga.backend.teacher.service.ReservationService;
import com.yoga.backend.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 예약 컨트롤러 클래스 강의 예약 생성 및 조회에 대한 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/teacher/reserve")
public class ReservationController {

    private final ReservationService reservationService; // 예약 서비스
    private final JwtUtil jwtUtil; // JWT 유틸리티

    @Autowired
    public ReservationController(ReservationService reservationService, JwtUtil jwtUtil) {
        this.reservationService = reservationService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 예약 생성 메서드
     *
     * @param reservationRequest 예약 요청 DTO
     * @param token              인증 토큰
     * @return 예약 생성 결과
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(
        @RequestBody ReservationRequestDto reservationRequest,
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token); // 토큰에서 사용자 ID 추출
        Map<String, Object> response = new HashMap<>();
        try {
            reservationService.createReservation(userId, reservationRequest);
            response.put("message", "성공");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 실시간 강의 조회 메서드
     *
     * @param token  인증 토큰
     * @param method 조회 방법
     * @return 실시간 강의 목록
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLiveLectures(
        @RequestHeader("Authorization") String token,
        @RequestParam int method) {
        try {
            List<LiveLectureDto> liveLectures = reservationService.getAllLiveLectures(
                method); // 모든 실시간 강의 조회
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data",
                liveLectures.stream().map(this::convertToResponse).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 강사별 실시간 강의 조회 메서드
     *
     * @param teacherId 강사 ID
     * @param method    조회 방법
     * @return 강사의 실시간 강의 목록
     */
    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getLiveLecturesByTeacherAndMethod(
        @PathVariable int teacherId,
        @RequestParam int method) {
        try {
            List<LiveLectureDto> liveLectures = reservationService.getLiveLecturesByTeacherAndMethod(
                teacherId, method); // 강사별 실시간 강의 조회
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data",
                liveLectures.stream().map(this::convertToResponse).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * LiveLectureDto를 Map 형태로 변환하는 메서드
     *
     * @param dto LiveLectureDto 객체
     * @return 변환된 Map 객체
     */
    private Map<String, Object> convertToResponse(LiveLectureDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("liveId", dto.getLiveId()); // 실시간 강의 ID
        response.put("liveTitle", dto.getLiveTitle()); // 실시간 강의 제목
        response.put("availableDay", dto.getAvailableDay()); // 실시간 강의 가능한 요일
        response.put("startDate", dto.getStartDate().toEpochMilli()); // 실시간 강의 시작 날짜
        response.put("endDate", dto.getEndDate().toEpochMilli()); // 실시간 강의 종료 날짜
        response.put("startTime", dto.getStartTime().toEpochMilli()); // 실시간 강의 시작 시간
        response.put("endTime", dto.getEndTime().toEpochMilli()); // 실시간 강의 종료 시간
        return response;
    }
}
