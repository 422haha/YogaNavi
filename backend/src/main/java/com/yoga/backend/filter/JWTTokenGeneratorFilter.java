package com.yoga.backend.filter;

import com.yoga.backend.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

            // Access Token 생성
            String accessToken = Jwts.builder()
                .issuer("Yoga Navi")
                .subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("role", populateRole(authentication))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();

            // Refresh Token 생성
            String refreshToken = Jwts.builder()
                .issuer("Yoga Navi")
                .subject("Refresh Token")
                .claim("username", authentication.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();

            // 생성된 토큰들을 응답 헤더에 추가
            response.setHeader(SecurityConstants.JWT_HEADER, accessToken);
            response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, refreshToken);
        }

        filterChain.doFilter(request, response);
    }

    private String populateRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse("");
    }
}