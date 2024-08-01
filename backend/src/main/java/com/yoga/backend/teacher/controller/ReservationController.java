package com.yoga.backend.teacher.controller;

import com.yoga.backend.common.entity.Reservation;
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

/**
 * 예약 컨트롤러 클래스
 */
@RestController
@RequestMapping("/teacher/reserve")
public class ReservationController {

    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ReservationController(ReservationService reservationService, JwtUtil jwtUtil) {
        this.reservationService = reservationService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 예약을 생성합니다.
     *
     * @param reservationRequest 예약 요청 DTO
     * @param token              인증 토큰
     * @param csrfToken          CSRF 토큰
     * @return 생성된 예약 응답
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(
        @RequestBody ReservationRequestDto reservationRequest,
        @RequestHeader("Authorization") String token,
        @RequestHeader("CSRF-Token") String csrfToken) {
        int userId = jwtUtil.getUserIdFromToken(token);
        try {
            Reservation reservation = reservationService.createReservation(userId,
                reservationRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 실시간 강의 목록을 조회합니다.
     *
     * @param token  인증 토큰
     * @param method 수업 방식 (0: 1대1, 1: 1대다)
     * @return 실시간 강의 목록 응답
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLiveLectures(
        @RequestHeader("Authorization") String token,
        @RequestParam int method) {
        try {
            List<LiveLectureDto> liveLectures = reservationService.getAllLiveLectures(method);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", liveLectures);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 특정 사용자 ID로 예약 목록을 조회합니다.
     *
     * @param token 인증 토큰
     * @return 사용자 예약 목록 응답
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserReservations(
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        try {
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", reservations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 특정 강사 ID로 예약 목록을 조회합니다.
     *
     * @param teacherId 강사 ID
     * @return 강사 예약 목록 응답
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherReservations(@PathVariable int teacherId) {
        try {
            List<Reservation> reservations = reservationService.getTeacherReservations(teacherId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", reservations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
