package com.yoga.backend.common.filter;

import com.yoga.backend.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    /**
     * 요청 내에서 한 번만 실행되는 필터 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 JWT 토큰과 리프레시 토큰을 가져옴
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        String refreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER);

        // JWT 토큰이 존재하는 경우
        if (null != jwt) {
            try {
                // JWT 키로 HMAC-SHA 키를 생성
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                // JWT 토큰을 검증하고 클레임을 파싱
                Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

                // 클레임에서 사용자 이름과 권한 정보 추출
                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("role");

                // 사용자 인증 객체 생성 및 SecurityContext에 설정
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 다음 필터 체인 실행
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                // JWT 토큰이 만료된 경우 리프레시 토큰 처리
                System.out.println("Access token expired. Attempting to use refresh token.");
                handleRefreshToken(request, response, filterChain, refreshToken);
            } catch (JwtException e) {
                // JWT 처리 실패 시 401 Unauthorized 응답 반환
                System.out.println("JWT processing failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT processing failed: " + e.getMessage());
            }
        } else {
            // JWT 토큰이 존재하지 않는 경우 다음 필터 체인 실행
            System.out.println("No JWT token found in request headers.");
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 리프레시 토큰 처리 메서드
     */
    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain, String refreshToken) throws IOException, ServletException {
        // 리프레시 토큰이 존재하는 경우
        if (refreshToken != null) {
            try {
                // JWT 키로 HMAC-SHA 키를 생성
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
                // 리프레시 토큰 검증 및 클레임 파싱
                Claims refreshClaims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

                // 새로운 액세스 토큰 생성
                String newAccessToken = Jwts.builder()
                    .issuer("Yoga Navi")
                    .subject("JWT Token")
                    .claim("username", refreshClaims.get("username"))
                    .claim("role", refreshClaims.get("role"))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis()
                        + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
                    .signWith(key)
                    .compact();

                // 새로운 액세스 토큰을 응답 헤더에 설정하고 다음 필터 체인 실행
                response.setHeader(SecurityConstants.JWT_HEADER, newAccessToken);
                filterChain.doFilter(request, response);
            } catch (JwtException e) {
                // 리프레시 토큰 처리 실패 시 401 Unauthorized 응답 반환
                System.out.println("Refresh token processing failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter()
                    .write("Both Access Token and Refresh Token are invalid. Please log in again.");
            }
        } else {
            // 리프레시 토큰이 존재하지 않는 경우 401 Unauthorized 응답 반환
            System.out.println("No refresh token provided.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access Token has expired and no Refresh Token provided.");
        }
    }
}
