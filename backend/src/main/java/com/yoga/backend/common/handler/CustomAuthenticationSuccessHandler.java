package com.yoga.backend.common.handler;

import com.yoga.backend.common.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        // JWT 생성을 위한 비밀 키 생성
        SecretKey key = Keys.hmacShaKeyFor(
            SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

        // 액세스 토큰 생성
        String accessToken = Jwts.builder()
            .issuer("Yoga Navi")
            .subject("JWT Token")
            .claim("username", authentication.getName())
            .claim("role", authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse(""))
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
            .signWith(key)
            .compact();

        // 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
            .issuer("Yoga Navi")
            .subject("Refresh Token")
            .claim("username", authentication.getName())
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION))
            .signWith(key)
            .compact();

        // 생성된 토큰 정보 출력
        System.out.println("access token: " + accessToken);
        System.out.println("refresh token: " + refreshToken);

        // 응답 헤더에 토큰 정보 추가
        response.setHeader(SecurityConstants.JWT_HEADER, accessToken);
        response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, refreshToken);
    }
}