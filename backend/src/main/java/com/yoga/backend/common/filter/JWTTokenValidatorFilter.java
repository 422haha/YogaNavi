package com.yoga.backend.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoga.backend.common.constants.SecurityConstants;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> data = new HashMap<>();

    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public JWTTokenValidatorFilter(JwtUtil jwtUtil, UsersRepository usersRepository) {
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        String refreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER);

        System.out.println("================================     jwt" + jwt);
        System.out.println("================================     refreshtoekn" + refreshToken);

        if (null != jwt && jwt.startsWith("Bearer ")) {
            jwt = jwtUtil.extractToken(jwt);
            try {
                JwtUtil.TokenStatus tokenStatus = jwtUtil.isTokenValid(jwt);
                switch (tokenStatus) {
                    case VALID:
                        String email = jwtUtil.getEmailFromToken(jwt);
                        String role = jwtUtil.getRoleFromToken(jwt);
                        Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
                            AuthorityUtils.createAuthorityList("ROLE_" + role.toUpperCase()));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        filterChain.doFilter(request, response);
                        break;
                    case INVALID:
                        sendUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
                        break;
                    case NOT_FOUND:
                        sendUnauthorizedResponse(response, "세션을 찾을 수 없습니다. 다시 로그인해주세요.");
                        break;
                    case EXPIRED:
                        if (null == refreshToken) {
                            sendUnauthorizedResponse(response, "Refresh-Token-Request");
                        } else {
                            handleRefreshToken(request, response, filterChain, refreshToken);
                        }
                        break;
                }

            } catch (Exception e) {
                log.error("액세스 토큰 처리 불가 {}", e);
                sendUnauthorizedResponse(response, "액세스 토큰 처리 불가");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain, String refreshToken) throws IOException, ServletException {
        if (refreshToken != null) {
            try {
                Claims refreshClaims = jwtUtil.validateToken(refreshToken);

                String email = (String) refreshClaims.get("email");

                Optional<Users> userOptional = usersRepository.findByEmail(email);

                if (userOptional.isPresent()) {
                    Users user = userOptional.get();
                    String role = user.getRole(); // 사용자의 역할을 가져옴

                    String newAccessToken = jwtUtil.generateAccessToken(email, role);

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setHeader(SecurityConstants.JWT_HEADER, newAccessToken);
                    response.getWriter().flush();
                } else {
                    sendUnauthorizedResponse(response, "사용자를 찾을 수 없습니다.");
                }

            } catch (ExpiredJwtException e) {
                sendRefreshTokenExpired(response, "리프레시 토큰 만료. 재로그인 필요");
            } catch (JwtException e) {
                sendUnauthorizedResponse(response, "리프레시 토큰 처리 실패");
            }
        } else {
            sendUnauthorizedResponse(response, "리프레시 토큰 없음");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message)
        throws IOException {
        if (message.equals("Refresh-Token-Request")) {
            response.setHeader("Refresh-Token-Request", "true");
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", message);
        data.put("data", new Object[]{});
        response.getWriter().write(objectMapper.writeValueAsString(data));
        response.getWriter().flush();
    }

    private void sendRefreshTokenExpired(HttpServletResponse response, String message)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        data.put("message", message);
        data.put("data", new Object[]{});
        response.getWriter().write(objectMapper.writeValueAsString(data));
        response.getWriter().flush();
    }
}
