package com.yoga.backend.users;

import com.yoga.backend.common.entity.Users;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private JavaMailSender emailSender;

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 서비스
    @Override
    public Users registerUser(RegisterDto registerDto) {
        String hashPwd = passwordEncoder.encode(registerDto.getPassword());
        registerDto.setPassword(hashPwd);

        Users users = new Users();
        users.setPwd(registerDto.getPassword());
        users.setEmail(registerDto.getEmail());
        users.setNickname(registerDto.getNickname());
        if (registerDto.isTeacher()) {
            users.setRole("TEACHER");
        } else {
            users.setRole("STUDENT");
        }

        return usersRepository.save(users);
    }

    // 이메일 중복 확인 서비스
    @Override
    public boolean checkUser(String email) {
        List<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty()) {
            return true;
        }
        return false;
    }

    // 인증번호 전송 서비스
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}