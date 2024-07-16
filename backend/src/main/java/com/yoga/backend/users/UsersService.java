package com.yoga.backend.users;

import com.yoga.backend.common.entity.Users;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);
}
