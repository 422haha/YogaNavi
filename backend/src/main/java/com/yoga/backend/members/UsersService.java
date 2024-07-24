package com.yoga.backend.members;

import com.yoga.backend.common.entity.Users;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    boolean checkNickname(String nickname);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);

    String sendPasswordResetToken(String email);

    boolean validateResetToken(String email, String token);

    String resetPassword(String email, String newPassword);

    List<Users> getUserByEmail(String email);


}
