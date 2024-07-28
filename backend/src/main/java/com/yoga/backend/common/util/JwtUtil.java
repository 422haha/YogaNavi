package com.yoga.backend.common.util;

import com.yoga.backend.common.constants.SecurityConstants;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    public enum TokenStatus {
        VALID,
        INVALID,
        NOT_FOUND
    }

    private final UsersRepository userRepository;

    @Autowired
    public JwtUtil(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final SecretKey key = Keys.hmacShaKeyFor(
        SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

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
        String email = claims.get("email", String.class);

        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get().getId();
        } else {
            throw new RuntimeException("User not found for email: " + email);
        }
    }

    public String getRoleFromToken(String bearerToken) {
        String token = extractToken(bearerToken);
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }


    //=============아래로 동시성 고려
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

        // Redis에 토큰 저장 (동시성 고려)
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForValue()
                    .set(email, token, SecurityConstants.ACCESS_TOKEN_EXPIRATION,
                        TimeUnit.MILLISECONDS);
                return operations.exec();
            }
        });

        return token;
    }

    public void invalidateToken(String email) {
        // Redis에서 토큰 삭제 (동시성 고려)
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.delete(email);
                return operations.exec();
            }
        });
    }


    public TokenStatus isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            String email = claims.get("email", String.class);

            // Redis에서 토큰 확인 (동시성 고려)
            return redisTemplate.execute(new SessionCallback<TokenStatus>() {
                @Override
                public TokenStatus execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    String storedToken = (String) operations.opsForValue().get(email);
                    List<Object> results = operations.exec();
                    if (results != null && !results.isEmpty()) {
                        String retrievedToken = (String) results.get(0);
                        if (retrievedToken == null) {
                            return TokenStatus.NOT_FOUND;
                        }
                        return token.equals(retrievedToken) ? TokenStatus.VALID
                            : TokenStatus.INVALID;
                    }
                    return TokenStatus.NOT_FOUND;
                }
            });
        } catch (Exception e) {
            return TokenStatus.INVALID;
        }
    }

    public boolean logout(String token) {
        try {
            String email = getEmailFromToken(token);
            log.info("Logging out user: {}", email);

            Long result = redisTemplate.execute(new SessionCallback<Long>() {
                @Override
                public Long execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.delete(email);
                    List<Object> results = operations.exec();
                    return (Long) results.get(0);
                }
            });

            if (result != null && result > 0) {
                log.info("Successfully logged out user: {}" + email);
                return true;
            } else {
                log.warn(
                    "Failed to logout user: {}. Token might not exist in Redis." + email);
                return false;
            }
        } catch (Exception e) {
            log.error("Error during logout: " + e);
            return false;
        }
    }
}
