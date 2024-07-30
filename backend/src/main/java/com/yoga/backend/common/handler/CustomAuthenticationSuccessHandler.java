package com.yoga.backend.common.handler;

import com.google.gson.Gson;
import com.yoga.backend.common.constants.SecurityConstants;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

/**
 * 인증 성공 시 처리하는 핸들러
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public CustomAuthenticationSuccessHandler(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        String fcmToken = request.getHeader("FCM-TOKEN");
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse("");

        jwtUtil.invalidateToken(email);
        Optional<Users> users = usersRepository.findByEmail(email);
        if (users.isPresent() && fcmToken != null && !fcmToken.isEmpty()) {
            Users user = users.get();
            user.setFcmToken(fcmToken);
            usersRepository.save(user);
        }

        String accessToken = jwtUtil.generateAccessToken(email, role);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        log.info("accessToken={}", accessToken);
        log.info("refreshToken={}", refreshToken);

        response.setHeader(SecurityConstants.JWT_HEADER, accessToken);
        response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, refreshToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(getResponseBody(role));
    }

    private String getResponseBody(String role) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "로그인 성공");
        responseBody.put("data", role.equals("TEACHER"));

        return new Gson().toJson(responseBody);
    }
}