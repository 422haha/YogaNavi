package com.yoga.backend.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> data = new HashMap<>();
        log.warn("로그인 실패: {}", exception.getMessage());
        data.put("message", "로그인 실패: " + exception.getMessage());
        data.put("data", new Object[]{});
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

}