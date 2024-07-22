package com.yoga.backend.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoga.backend.common.constants.SecurityConstants;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.filter.OncePerRequestFilter;


public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> data = new HashMap<>();

    private final JwtUtil jwtUtil;
    private final UsersRepository userRepository;

    public JWTTokenValidatorFilter(UsersRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        String refreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER);

        if (null != jwt && jwt.startsWith("Bearer ")) {
            jwt = jwtUtil.extractToken(jwt);
            try {
                Claims claims = jwtUtil.validateToken(jwt);
                String email = String.valueOf(claims.get("email"));
                String authorities = (String) claims.get("role");

                if (jwtUtil.validateUserToken(email, jwt)) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                } else {
                    sendUnauthorizedResponse(response, "다른 기기에서 로그인됨");
                }
            } catch (ExpiredJwtException e) {
                if (null == refreshToken) {
                    sendUnauthorizedResponse(response, "리프레시 토큰 요청");
                } else {
                    handleRefreshToken(request, response, filterChain, refreshToken);
                }
            } catch (JwtException e) {
                sendUnauthorizedResponse(response, "액세스 토큰 처리 불가");
            } catch (Exception e) {
                // 예외 발생 시 트랜잭션 롤백
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                sendUnauthorizedResponse(response, "인증 처리 중 오류 발생");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain, String refreshToken) throws IOException, ServletException {
        if (refreshToken != null) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
                Claims refreshClaims = jwtUtil.validateToken(refreshToken);

                String email = (String) refreshClaims.get("email");
                String role = (String) refreshClaims.get("role");

                String newAccessToken = jwtUtil.generateAccessToken(email, role);

                List<Users> userList = userRepository.findByEmail(email);
                if (!userList.isEmpty()) {
                    jwtUtil.updateUserTokenAndLogoutOthers(email, newAccessToken);

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setHeader(SecurityConstants.JWT_HEADER, newAccessToken);
                    filterChain.doFilter(request, response);
                } else {
                    sendUnauthorizedResponse(response, "사용자를 찾을 수 없음");
                }
            } catch (ExpiredJwtException e) {
                sendUnauthorizedResponse(response, "리프레시 토큰 만료. 재로그인 필요");
            } catch (JwtException e) {
                sendUnauthorizedResponse(response, "리프레시 토큰 처리 실패");
            }
        } else {
            sendUnauthorizedResponse(response, "리프레시 토큰 없음");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", message);
        data.put("data", new Object[]{});
        response.getWriter().write(objectMapper.writeValueAsString(data));
        response.getWriter().flush();
    }
}
