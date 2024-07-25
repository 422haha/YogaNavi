package com.yoga.backend.members;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/*
 * 회원가입, 비밀번호 재설정, 회원정보 수정, 로그아웃 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/members")
public class UserController {


    // 인증번호 저장용 변수
    static int rNum;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    public UserController(UsersService usersService, JwtUtil jwtUtil) {
        this.usersService = usersService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 회원 가입 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 회원 가입 결과
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        boolean check = usersService.checkNickname(registerDto.getNickname());
        if (check) {
            try {
                Users savedUsers = usersService.registerUser(registerDto);
                if (savedUsers.getId() > 0) {
                    log.info("회원가입 성공");
                    response.put("message", "회원가입 성공");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }
            } catch (Exception ex) {
                log.info("회원가입 불가={}", ex.getMessage());
                response.put("message", "회원가입 불가" + ex.getMessage());
                response.put("data", new Object[]{});

                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
            }
        } else {
            log.info("닉네임 중복");
            response.put("message", "닉네임 중복");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        log.info("회원가입 실패");
        response.put("message", "회원가입 실패");
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 회원가입을 위한 인증번호 전송 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 인증번호 전송 결과
     */
    @PostMapping("/register/email")
    public ResponseEntity<Map<String, Object>> registerUserEmail(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("in email : " + registerDto.getEmail());
        boolean check = usersService.checkUser(registerDto.getEmail());
        if (check) {
            int randNum = (int) (Math.random() * 899999) + 100000;
            rNum = randNum;
            usersService.sendSimpleMessage(registerDto.getEmail(), "Yoga Navi 회원가입 인증번호",
                "회원가입 인증번호 : " + rNum);

            response.put("message", "인증 번호 전송");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "이미 존재하는 회원");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * 회원가입을 위한 인증번호 확인 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 인증번호 확인 결과
     */
    @PostMapping("/register/authnumber")
    public ResponseEntity<Map<String, Object>> checkAuthNumber(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        if (registerDto.getAuthnumber() == rNum) {
            response.put("message", "인증 완료");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "틀린 번호");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 비밀번호 재설정을 위한 인증번호 전송 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 인증번호 전송 결과
     */
    @PostMapping("/find-password/email")
    public ResponseEntity<Map<String, Object>> passwordUserEmail(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        String result = usersService.sendPasswordResetToken(registerDto.getEmail());
        response.put("message", result);
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 인증번호 확인 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 인증번호 확인 결과
     */
    @PostMapping("/find-password/authnumber")
    public ResponseEntity<Map<String, Object>> passwordCheckAuthNumber(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = usersService.validateResetToken(registerDto.getEmail(),
            String.valueOf(registerDto.getAuthnumber()));
        if (isValid) {
            response.put("message", "인증 완료");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "틀린 번호");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 비밀번호 변경 컨트롤러
     *
     * @param registerDto 회원 가입 정보
     * @return 변경 결과
     */
    @PostMapping("/find-password")
    public ResponseEntity<Map<String, Object>> setPassword(@RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        String result = usersService.resetPassword(registerDto.getEmail(),
            registerDto.getPassword());
        response.put("message", result);
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용자 정보 수정. 정보 가져오기
     *
     * @param token 토큰
     * @return 회원 정보, 선생이라면 해시태그도
     *
     * @TODO 선생이면 해시태그도 반환
     */
    @GetMapping("/update")
    public ResponseEntity<Map<String, Object>> getMyInfo(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = jwtUtil.getEmailFromToken(token);
            Users user = usersService.getUserByEmail(email);

            if (user!=null) {

                RegisterDto registerDto = new RegisterDto();
                registerDto.setImageUrl(user.getProfile_image_url());
                registerDto.setNickname(user.getNickname());
                registerDto.setTeacher(jwtUtil.getRoleFromToken(token).equals("TEACHER"));

                response.put("message", "success");
                response.put("data", registerDto);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "User not found");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error retrieving user information: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용자 정보 수정. 정보 수정
     *
     * @param token 토큰
     * @return 수정 후 회원 정보, 선생이라면 해시태그도
     *
     * @TODO 선생이면 해시태그도 반환
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(
        @RequestHeader("Authorization") String token, @RequestBody UpdateDto updateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = jwtUtil.getEmailFromToken(token);
            Users user = usersService.updateUser(updateDto, email);
            if (user != null) {
                RegisterDto registerDto = new RegisterDto();
                registerDto.setImageUrl(user.getProfile_image_url());
                registerDto.setNickname(user.getNickname());
                registerDto.setTeacher(jwtUtil.getRoleFromToken(token).equals("TEACHER"));

                response.put("message", "success");
                response.put("data", registerDto);
                return ResponseEntity.ok(response);
            }else{
                response.put("message", "User not found");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error retrieving user information: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}