package com.yoga.backend.filter;

import com.yoga.backend.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        if (null != jwt) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("role");

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Access Token이 만료되었을 때
                String refreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER);
                if (refreshToken != null) {
                    try {
                        // Refresh Token 검증
                        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
                        Claims refreshClaims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(refreshToken)
                            .getPayload();

                        // 새로운 Access Token 생성
                        String newAccessToken = Jwts.builder()
                            .issuer("Yoga Navi")
                            .subject("JWT Token")
                            .claim("username", refreshClaims.get("username"))
                            .claim("role", refreshClaims.get("role"))
                            .issuedAt(new Date())
                            .expiration(new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
                            .signWith(key)
                            .compact();

                        // 새로운 Access Token을 응답 헤더에 추가
                        response.setHeader(SecurityConstants.JWT_HEADER, newAccessToken);
                    } catch (Exception refreshException) {
                        // Refresh Token도 만료된 경우
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Both Access Token and Refresh Token have expired. Please log in again.");
                        return;
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Access Token has expired and no Refresh Token provided.");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}