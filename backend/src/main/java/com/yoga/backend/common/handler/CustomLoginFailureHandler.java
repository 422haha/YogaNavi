package com.yoga.backend.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        // 인증 실패 시 HTTP 상태 코드 401(Unauthorized) 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 실패 메시지와 빈 데이터 객체를 JSON 형태로 응답
        Map<String, Object> data = new HashMap<>();
        data.put("message", "로그인 실패: " + exception.getMessage());
        data.put("data", new Object[]{});
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

}