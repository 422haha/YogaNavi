package com.yoga.backend.members;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import com.yoga.backend.members.service.UsersService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * 회원가입, 비밀번호 재설정, 회원정보 수정, 로그아웃 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UsersService usersService;

    public UserController(UsersService usersService,
        JwtUtil jwtUtil) {
        this.usersService = usersService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 회원 가입을 처리
     *
     * @param registerDto 회원 가입에 필요한 정보 DTO
     * @return 회원 가입 결과
     */
    @PostMapping("/members/register")
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
     * 회원가입을 위한 인증번호를 이메일로 전송
     *
     * @param registerDto 이메일 정보를 담은 DTO
     * @return 인증번호 전송 결과
     */
    @PostMapping("/members/register/email")
    public ResponseEntity<Map<String, Object>> registerUserEmail(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        String result = usersService.sendEmailVerificationToken(registerDto.getEmail());
        response.put("message", result);
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 회원가입 시 전송된 인증번호를 확인
     *
     * @param registerDto 인증번호를 담은 DTO
     * @return 인증번호 확인 결과
     */
    @PostMapping("/members/register/authnumber")
    public ResponseEntity<Map<String, Object>> checkAuthNumber(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = usersService.validateEmailAuthToken(registerDto.getEmail(),
            String.valueOf(registerDto.getAuthnumber()));
        if (isValid) {
            response.put("message", "인증 완료");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "인증 실패 (잘못된 인증번호 또는 만료)");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 비밀번호 재설정을 위한 인증번호를 이메일로 전송
     *
     * @param registerDto 이메일 정보를 담은 DTO
     * @return 인증번호 전송 결과
     */
    @PostMapping("/members/find-password/email")
    public ResponseEntity<Map<String, Object>> passwordUserEmail(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        String result = usersService.sendPasswordResetToken(registerDto.getEmail());
        response.put("message", result);
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 비밀번호 재설정을 위한 인증번호를 확인
     *
     * @param registerDto 인증번호를 담은 DTO
     * @return 인증번호 확인 결과
     */
    @PostMapping("/members/find-password/authnumber")
    public ResponseEntity<Map<String, Object>> passwordCheckAuthNumber(
        @RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = usersService.validatePasswordAuthToken(registerDto.getEmail(),
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
     * 비밀번호를 변경
     *
     * @param registerDto 새 비밀번호 정보를 담은 DTO
     * @return 비밀번호 변경 결과
     */
    @PostMapping("/members/find-password")
    public ResponseEntity<Map<String, Object>> setPassword(@RequestBody RegisterDto registerDto) {
        Map<String, Object> response = new HashMap<>();
        String result = usersService.resetPassword(registerDto.getEmail(),
            registerDto.getPassword());
        response.put("message", result);
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용자 정보를 조회
     *
     * @param token 사용자 인증 토큰
     * @return 사용자 정보를 담은 ResponseEntity
     */
    @GetMapping("/mypage/info")
    public ResponseEntity<Map<String, Object>> getMyInfo(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            Users user = usersService.getUserByUserId(userId);

            if (user != null) {
                UpdateDto responseDto = new UpdateDto();
                boolean isTeacher = jwtUtil.getRoleFromToken(token).equals("TEACHER");
                responseDto.setImageUrl(user.getProfile_image_url());
                responseDto.setImageUrlSmall(user.getProfile_image_url_small());
                responseDto.setNickname(user.getNickname());
                responseDto.setTeacher(isTeacher);

                if (isTeacher) {
                    Set<String> myTags = usersService.getUserHashtags(userId);
                    List<String> tags = new ArrayList<>(myTags);
                    responseDto.setHashTags(tags);
                    responseDto.setContent(user.getContent());
                }

                response.put("message", "조회 성공");
                response.put("data", responseDto);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "사용자 찾을 수 없음");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "내 정보 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용자 정보 수정 전에 비밀번호 확인
     *
     * @param token     사용자 인증 토큰
     * @param updateDto 비밀번호 담은 DTO
     * @return 비밀번호 일치 응답
     */
    @PostMapping("/mypage/check")
    public ResponseEntity<Map<String, Object>> checkBeforeUpdate(
        @RequestHeader("Authorization") String token, @RequestBody UpdateDto updateDto) {
        Map<String, Object> response = new HashMap<>();

        int userId = jwtUtil.getUserIdFromToken(token);

        boolean result = usersService.checkPwd(userId,
            updateDto.getPassword());
        if (result) {
            response.put("message", "success");
            response.put("data", true);
        } else {
            response.put("message", "fail");
            response.put("data", false);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용자 정보 수정
     *
     * @param token     사용자 인증 토큰
     * @param updateDto 수정할 사용자 정보를 담은 DTO
     * @return 수정된 사용자 정보를 담은 ResponseEntity
     */
    @PostMapping("/mypage/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(
        @RequestHeader("Authorization") String token, @RequestBody UpdateDto updateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            Users user = usersService.updateUser(updateDto, userId);
            if (user != null) {
                UpdateDto responseDto = new UpdateDto();
                boolean isTeacher = jwtUtil.getRoleFromToken(token).equals("TEACHER");
                responseDto.setImageUrl(user.getProfile_image_url());
                responseDto.setImageUrlSmall(user.getProfile_image_url_small());
                responseDto.setNickname(user.getNickname());
                responseDto.setTeacher(isTeacher);

                if (isTeacher) {
                    Set<String> myTags = usersService.getUserHashtags(userId);
                    List<String> tags = new ArrayList<>(myTags);
                    responseDto.setHashTags(tags);
                    responseDto.setContent(user.getContent());
                }

                response.put("message", "수정 완료");
                response.put("data", responseDto);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "사용자 찾을 수 없음");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("message", "수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 로그아웃
     *
     * @param token 사용자 인증 토큰
     * @return 로그아웃 처리 결과
     */
    @PostMapping("/mylogout")
    public ResponseEntity<Map<String, Object>> logout(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("로그아웃 요청");
            boolean logoutSuccess = jwtUtil.logout(token);
            if (logoutSuccess) {
                log.info("로그아웃 성공");
                response.put("message", "로그아웃 성공");
                response.put("data", new Object[]{});
                return ResponseEntity.ok(response);
            } else {
                log.warn("로그아웃 실패");
                response.put("message", "로그아웃 실패");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            log.error("로그아웃중 에러 발생: " + e);
            response.put("message", "로그아웃 처리 중 오류 발생");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 사용자 탈퇴
     *
     * @param token 사용자 인증 토큰
     * @return 삭제 요청 처리 결과
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> requestDeleteUser(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            usersService.requestDeleteUser(userId);
            jwtUtil.logout(token);  // 회원 탈퇴 시 로그아웃 처리
            response.put("message",
                "탈퇴 요청이 성공적으로 처리되었습니다. 7일 후에 계정이 삭제됩니다. 7일 이내에 로그인 시 자동으로 탈퇴가 취소됩니다.");
            response.put("data", new Object[]{});
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "탈퇴 요청 처리 중 오류 발생");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}