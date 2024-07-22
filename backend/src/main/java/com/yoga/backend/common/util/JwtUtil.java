package com.yoga.backend.common.util;

import com.yoga.backend.common.constants.SecurityConstants;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JwtUtil {

    @Autowired
    private UsersRepository userRepository;

    private final SecretKey key = Keys.hmacShaKeyFor(
        SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

    // access token 생성
    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
            .issuer("Yoga Navi")
            .subject("JWT Token")
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
            .signWith(key)
            .compact();
    }

    // refresh token 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
            .issuer("Yoga Navi")
            .subject("Refresh Token")
            .claim("email", email)
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION))
            .signWith(key)
            .compact();
    }

    // 토큰 검증
    public Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String extractToken(String bearerToken) {
        return bearerToken.substring(7);
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        Claims claims = validateToken(token);
        return claims.get("email", String.class);

    }

    // 사용자의 토큰을 검증함.
    public boolean validateUserToken(String email, String token) {
        List<Users> userList = userRepository.findByEmail(email);
        if (!userList.isEmpty()) {
            Users user = userList.get(0);
            return token.equals(user.getActiveToken());
        }
        return false;
    }

    // 사용자의 토큰을 업데이트하고 이전 세션을 로그아웃
    @Transactional
    public void updateUserTokenAndLogoutOthers(String email, String newToken) {
        List<Users> userList = userRepository.findByEmail(email);
        if (!userList.isEmpty()) {
            Users user = userList.get(0);
            // 이전 토큰 저장
            String oldToken = user.getActiveToken();
            // 새 토큰으로 업데이트
            user.setActiveToken(newToken);
            userRepository.save(user);
        }
    }

    public void logoutUser(String email) {
        List<Users> userList = userRepository.findByEmail(email);
        if (!userList.isEmpty()) {
            Users user = userList.get(0);
            String oldToken = user.getActiveToken();

            // 활성 토큰을 null로 설정
            user.setActiveToken(null);
            userRepository.save(user);

        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            String email = claims.get("email", String.class);
            return validateUserToken(email, token);
        } catch (Exception e) {
            return false;
        }
    }
}
