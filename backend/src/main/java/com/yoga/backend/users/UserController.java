package com.yoga.backend.users;

import com.yoga.backend.common.entity.Users;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class UserController {

    static int rNum;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }


    //회원 가입 컨트롤러
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Users savedUsers = usersService.registerUser(registerDto);
            if (savedUsers.getId() > 0) {
                response.put("message", "회원가입 성공");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
        } catch (Exception ex) {
            response.put("message", "회원가입 불가");
            response.put("data", null);

            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
        }
        response.put("message", "회원가입 실패");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 인증번호 전송 컨트롤러
    @PostMapping("/register/email")
    public ResponseEntity<Map<String, Object>> registerUserEmail(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        boolean check = usersService.checkUser(registerDto.getEmail());
        if (check) {
            int randNum = (int) (Math.random() * 899999) + 100000;
            rNum = randNum;
            usersService.sendSimpleMessage(registerDto.getEmail(), "Yoga Navi 비밀번호 재설정 인증번호",
                "비밀번호 재설정 인증번호 : " + rNum);

            response.put("message", "인증 번호 전송");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "이미 존재하는 회원");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    //인증번호 확인 컨트롤러
    @PostMapping("/register/authnumber")
    public ResponseEntity<Map<String, Object>> checkAuthNumber(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        if (registerDto.getAuthnumber() == rNum) {
            response.put("message", "인증 완료");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "틀린 번호");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}