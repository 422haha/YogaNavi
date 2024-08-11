package com.yoga.backend.livelectures.Controller;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.livelectures.dto.LectureHistoryDto;
import com.yoga.backend.livelectures.service.HistoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage/course-history")
public class HistoryController {

    private final JwtUtil jwtUtil;
    private final HistoryService hsitoryService;

    public HistoryController(JwtUtil jwtUtil,
        HistoryService hsitoryService) {
        this.jwtUtil = jwtUtil;
        this.hsitoryService = hsitoryService;

    }

    /**
     * 수강 내역 처리
     *
     * @param token JWT 토큰
     * @return 수강 내역 페이지에 대한 응답 포함한 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData(
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        Map<String, Object> response = new HashMap<>();
        try {
            List<LectureHistoryDto> history = hsitoryService.getHistory(userId);
            response.put("message", "내 수강내역 조회 성공");
            response.put("data", history);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "내 수강내역 조회 실패");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
