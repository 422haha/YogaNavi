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
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JwtUtil {

    private final UsersRepository userRepository;

    @Autowired
    public JwtUtil(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final SecretKey key = Keys.hmacShaKeyFor(
        SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));


    // access token 생성
    public String generateAccessToken(String email, String role) {
        String token = Jwts.builder()
            .issuer("Yoga Navi")
            .subject("JWT Token")
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(
                new Date(System.currentTimeMillis() + SecurityConstants.ACCESS_TOKEN_EXPIRATION))
            .signWith(key)
            .compact();

        // 추가
        redisTemplate.opsForValue()
            .set(email, token, SecurityConstants.ACCESS_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);

        return token;
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

    public int getUserIdFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        Claims claims = validateToken(token);
        return userRepository.findByEmail(claims.get("email", String.class)).get(0).getId();
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

    // 추가
    public void invalidateToken(String email) {
        redisTemplate.delete(email);
    }

    // 추가
    public boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            String email = claims.get("email", String.class);
            String storedToken = redisTemplate.opsForValue().get(email);
            return token.equals(storedToken);
        } catch (Exception e) {
            return false;
        }
    }

}
