package com.yoga.backend.common.util;

import com.yoga.backend.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String getEmailFromToken(String token) {
        // JWT 키로 HMAC-SHA 키를 생성
        SecretKey key = Keys.hmacShaKeyFor(
            SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }
}
