package com.yoga.backend.common.filter;

import com.yoga.backend.common.constants.SecurityConstants;
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

            System.out.println("JWTTokenGeneratorFilter --------- equals login : "+SecurityContextHolder.getContext().getAuthentication());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (null != authentication && authentication.isAuthenticated()) {
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                // Access Token 생성
                String accessToken = Jwts.builder()
                    .issuer("Yoga Navi")
                    .subject("JWT Token")
                    .claim("username", authentication.getName())
                    .claim("role", populateRole(authentication))
                    .issuedAt(new Date())
                    .expiration(new Date(
                        System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
                    .signWith(key)
                    .compact();

                // Refresh Token 생성
                String refreshToken = Jwts.builder()
                    .issuer("Yoga Navi")
                    .subject("Refresh Token")
                    .claim("username", authentication.getName())
                    .claim("role", populateRole(authentication))
                    .issuedAt(new Date())
                    .expiration(new Date(
                        System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION))
                    .signWith(key)
                    .compact();

                System.out.println("accessToken: " + accessToken);
                System.out.println("refreshToken: " + refreshToken);
                // 생성된 토큰들을 응답 헤더에 추가
                response.setHeader(SecurityConstants.JWT_HEADER, accessToken);
                response.setHeader(SecurityConstants.REFRESH_TOKEN_HEADER, refreshToken);
            }

            filterChain.doFilter(request, response);

    }

    private String populateRole(Authentication authentication) {
        System.out.println("=================populateRole"+authentication+","+authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse(""));

        return authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse("");
    }
}
