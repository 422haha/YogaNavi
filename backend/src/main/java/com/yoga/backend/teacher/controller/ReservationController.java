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
import java.util.stream.Collectors;

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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(
        @RequestBody ReservationRequestDto reservationRequest,
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        try {
            reservationService.createReservation(userId, reservationRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLiveLectures(
        @RequestHeader("Authorization") String token,
        @RequestParam int method) {
        try {
            List<LiveLectureDto> liveLectures = reservationService.getAllLiveLectures(method);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", liveLectures.stream().map(this::convertToResponse).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다");
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

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

    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getLiveLecturesByTeacherAndMethod(
        @PathVariable int teacherId,
        @RequestParam int method) {
        try {
            List<LiveLectureDto> liveLectures = reservationService.getLiveLecturesByTeacherAndMethod(teacherId, method);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("data", liveLectures.stream().map(this::convertToResponse).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private Map<String, Object> convertToResponse(LiveLectureDto dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("liveId", dto.getLiveId());
        response.put("liveTitle", dto.getLiveTitle());
        response.put("availableDay", dto.getAvailableDay());
        response.put("startDate", dto.getStartDate().toEpochMilli());
        response.put("endDate", dto.getEndDate().toEpochMilli());
        response.put("startTime", dto.getStartTime().toEpochMilli());
        response.put("endTime", dto.getEndTime().toEpochMilli());
        return response;
    }
}
