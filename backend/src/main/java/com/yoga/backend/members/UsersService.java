package com.yoga.backend.members;

import com.yoga.backend.common.entity.Users;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    boolean checkNickname(String nickname);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);

    Users setPassword(RegisterDto registerDto);
}
