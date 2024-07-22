package com.yoga.backend.common.util;

import com.yoga.backend.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final UsersRepository userRepository;

    @Autowired
    public JwtUtil(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

        token = token.replace("Bearer ", "");


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

        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

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

//    // 사용자의 토큰을 검증함.
//    public boolean validateUserToken(String email, String token) {
//        List<Users> userList = userRepository.findByEmail(email);
//        if (!userList.isEmpty()) {
//            Users user = userList.get(0);
//            System.out.println("token: " + token + " user token: " + user.getActiveToken());
//            return token.equals(user.getActiveToken());
//        }
//        return false;
//    }

    // 사용자의 토큰을 업데이트하고 이전 세션을 로그아웃
    @Transactional
    public void updateUserTokenAndLogoutOthers(String email, String newToken) {
//        List<Users> userList = userRepository.findByEmail(email);
//        if (!userList.isEmpty()) {
//            Users user = userList.get(0);
//            System.out.println("============user : "+user.getEmail());
//            // 이전 토큰 저장
//            String oldToken = user.getActiveToken();
//            // 새 토큰으로 업데이트
//            user.setActiveToken(newToken);
//            System.out.println("");
//            userRepository.updateActiveToken(user.getId(), newToken);
//            System.out.println("====================="+newToken+"============user : "+user.getActiveToken());
//        }
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

//    public boolean isTokenValid(String token) {
//        try {
//            Claims claims = validateToken(token);
//            String email = claims.get("email", String.class);
//            return validateUserToken(email, token);
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
