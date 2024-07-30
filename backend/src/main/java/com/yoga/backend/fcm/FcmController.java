package com.yoga.backend.fcm;

import com.yoga.backend.common.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FcmController {

    private final JwtUtil jwtUtil;
    private final FCMService fcmService;

    public FcmController(FCMService fcmService, JwtUtil jwtUtil) {
        this.fcmService = fcmService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping("/fcm")
    public ResponseEntity<Map<String, Object>> fcm(@RequestHeader("Authorization") String token,
        @RequestHeader("FCM-TOKEN") String fcmToken) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        try {
            fcmService.setNewFcm(fcmToken, userId);
            response.put("message", "FCM TOKEN 갱신 완료");
            response.put("data", new Object[]{});
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error("fcm error, 서버 에러 : {}", e.getMessage());
            response.put("message", "FCM TOKEN 갱신 불가");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
