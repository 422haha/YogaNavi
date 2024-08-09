package com.yoga.backend.livelectures.Controller;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.livelectures.dto.HomeResponseDto;
import com.yoga.backend.livelectures.service.HomeService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 홈 컨트롤러 클래스. 홈 관련 요청 처리
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    private final JwtUtil jwtUtil;
    private final HomeService homeService;

    public HomeController(JwtUtil jwtUtil, HomeService homeService) {
        this.jwtUtil = jwtUtil;
        this.homeService = homeService;
    }

    /**
     * 홈 페이지 요청 처리
     *
     * @param token JWT 토큰
     * @return 홈 페이지에 대한 응답 DTO를 포함한 ResponseEntity 객체
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData(
        @RequestHeader("Authorization") String token) {
        int userId = jwtUtil.getUserIdFromToken(token);
        List<HomeResponseDto> homeData = homeService.getHomeData(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "내 화상 강의 할 일 조회 성공");
        response.put("data", homeData);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
