package com.yoga.backend.members;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Users;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JavaMailSender emailSender;

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 서비스
     *
     * @param registerDto 회원가입 정보
     * @return 저장된 사용자 정보
     */
    @Override
    public Users registerUser(RegisterDto registerDto) {
        // 비밀번호 해시화
        String hashPwd = passwordEncoder.encode(registerDto.getPassword());
        registerDto.setPassword(hashPwd);

        // 사용자 정보 생성
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

    /**
     * 이메일 중복 확인 서비스
     *
     * @param nickname 확인할 이메일
     * @return 이메일 중복 여부
     */
    @Override
    public boolean checkNickname(String nickname) {
        List<Users> users = usersRepository.findByNickname(nickname);
        if (users.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 이메일 중복 확인 서비스
     *
     * @param email 확인할 이메일
     * @return 이메일 중복 여부
     */
    @Override
    public boolean checkUser(String email) {
        List<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 인증번호 전송 서비스
     * @param to 수신자 이메일
     * @param subject 메일 제목
     * @param text 메일 내용
     */
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    /**
     * 인증번호 전송 서비스
     *
     * @param email 수신자 이메일
     *
     */
    @Transactional
    public String sendPasswordResetToken(String email) {
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            int randNum = (int) (Math.random() * 899999) + 100000;
            user.setResetToken(String.valueOf(randNum));
            usersRepository.save(user);

            sendSimpleMessage(email, "Yoga Navi 비밀번호 재설정 인증번호",
                "비밀번호 재설정 인증번호 : " + randNum);

            return "인증 번호 전송";
        } else {
            return "존재하지 않는 회원입니다.";
        }
    }

    /**
     * 인증번호 전송 서비스
     *
     * @param email 수신자 이메일
     * @param token 토큰값
     * @return 토큰값 일치 여부
     */
    @Transactional
    public boolean validateResetToken(String email, String token) {
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return token.equals(user.getResetToken());
        }
        return false;
    }


    /**
     * 비밀번호 재설정 서비스
     *
     * @param newPassword 회원 정보
     *
     */
    @Transactional
    public String resetPassword(String email, String newPassword) {
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            String hashPwd = passwordEncoder.encode(newPassword);
            user.setPwd(hashPwd);
            user.setResetToken(null);
            usersRepository.save(user);
            return "비밀번호 재설정 성공";
        }
        return "비밀번호 재설정 실패";
    }

    @Transactional(readOnly = true)
    public List<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }



}
