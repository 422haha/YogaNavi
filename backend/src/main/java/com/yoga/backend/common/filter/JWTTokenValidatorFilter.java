package com.yoga.backend.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> data = new HashMap<>();

    /**
     * 요청 내에서 한 번만 실행되는 필터 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 액세스 토큰과 리프레시 토큰을 가져옴
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        String refreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER);

        // JWT 토큰이 존재하는 경우
        if (null != jwt && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            try {
                // JWT 키로 HMAC-SHA 키를 생성
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                // 액세스 토큰을 검증하고 클레임을 파싱
                Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

                // 클레임에서 사용자 이름과 권한 정보 추출
                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("role");

                Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);

            } catch (ExpiredJwtException e) {
                // 액세스 토큰이 만료된 경우 리프레시 토큰 처리
                if (null == refreshToken) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("refresh_token_required","token_required");
                    data.put("message", "리프레시 토큰 요청");
                    data.put("data", new Object[]{});
                    response.getWriter().write(objectMapper.writeValueAsString(data));
                    response.getWriter().flush();
                } else {
                    handleRefreshToken(request, response, filterChain, refreshToken);
                }
            } catch (JwtException e) {
                // 액세스 토큰 처리 실패 시 401 Unauthorized 응답 반환
                data.put("message", "액세스 토큰 처리 불가");
                data.put("data", new Object[]{});
                response.getWriter().write(objectMapper.writeValueAsString(data));
                response.getWriter().flush();
            }
        } else {
            // 액세스 토큰이 존재하지 않는 경우 다음 필터 체인 실행
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
                Claims refreshClaims = jwtUtil.validateToken(refreshToken);

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

                String newAccessToken = jwtUtil.generateAccessToken(email, role);

                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setHeader(SecurityConstants.JWT_HEADER, newAccessToken);
                filterChain.doFilter(request, response);

            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                data.put("message", "리프레시 토큰 만료. 재로그인 필요");
                data.put("data", new Object[]{});
                response.getWriter().write(objectMapper.writeValueAsString(data));
                response.getWriter().flush();
            } catch (JwtException e) {
                // 리프레시 토큰 처리 실패 시 401 Unauthorized 응답 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                data.put("message", "리프레시 토큰 처리 실패");
                data.put("data", new Object[]{});
                response.getWriter().write(objectMapper.writeValueAsString(data));
                response.getWriter().flush();
            }
        } else {
            // 리프레시 토큰이 존재하지 않는 경우 401 Unauthorized 응답 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data.put("message", "리프레시 토큰 없음");
            data.put("data", new Object[]{});
            response.getWriter().write(objectMapper.writeValueAsString(data));
            response.getWriter().flush();
        }
    }
}
