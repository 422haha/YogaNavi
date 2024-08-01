package com.yoga.backend.members.service;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import java.util.Set;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface UsersService {

    Users registerUser(RegisterDto registerDto);

    void recoverAccount(Users user);

    boolean checkNickname(String nickname);

    boolean checkUser(String email);

    void sendSimpleMessage(String registerDto, String message, String s);

    String sendEmailVerificationToken(String email);

    String sendPasswordResetToken(String email);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    boolean validatePasswordAuthToken(String email, String token);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    boolean validateEmailAuthToken(String email, String token);


    String resetPassword(String email, String newPassword);

    Users getUserByUserId(int userId);

    Users updateUser(UpdateDto updateDto, int userId);

    Set<String> getUserHashtags(int userId);

    void updateUserHashtags(int userId, Set<String> newHashtags);

    void requestDeleteUser(int userId);

    void processDeletedUsers();
}
