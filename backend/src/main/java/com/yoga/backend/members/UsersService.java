package com.yoga.backend.members;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import java.util.List;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    boolean checkNickname(String nickname);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);

    String sendPasswordResetToken(String email);

    boolean validateResetToken(String email, String token);

    String resetPassword(String email, String newPassword);

    Users getUserByEmail(String email);


    Users updateUser(UpdateDto updateDto, String email);
}
