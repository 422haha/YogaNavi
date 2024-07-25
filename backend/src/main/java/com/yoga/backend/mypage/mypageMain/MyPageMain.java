package com.yoga.backend.mypage.mypageMain;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.UsersService;
import com.yoga.backend.members.dto.RegisterDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
public class MyPageMain {

    private final JwtUtil jwtUtil;
    private final MyPageMainService myPageMainService;

    public MyPageMain(MyPageMainService myPageMainService, JwtUtil jwtUtil) {
        this.myPageMainService = myPageMainService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> index(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            Users user = myPageMainService.getUserInfo(userId);
            if (user != null) {
                MyPageDto myPageDto = new MyPageDto();
                myPageDto.setImageUrl(user.getProfile_image_url());
                myPageDto.setNickname(user.getNickname());
                myPageDto.setTeacher(jwtUtil.getRoleFromToken(token).equals("TEACHER"));

                response.put("message", "success");
                response.put("data", myPageDto);
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

}

