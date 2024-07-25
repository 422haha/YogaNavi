package com.yoga.backend.members;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JavaMailSender emailSender;

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private HashtagRepository hashtagRepository;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder, HashtagRepository hashtagRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.hashtagRepository = hashtagRepository;
    }

    /**
     * 회원가입 서비스
     *
     * @param registerDto 회원가입 정보
     * @return 저장된 사용자 정보
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
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
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
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
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
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

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).get(0);
    }

    @Override
    public Users updateUser(UpdateDto updateDto, String email){
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            if(updateDto.getNickname()!=null){
                user.setNickname(updateDto.getNickname());
            }
            if(updateDto.getImageUrl()!=null){
                user.setProfile_image_url(updateDto.getImageUrl());
            }
            if(updateDto.getPassword()!=null){
                user.setPwd(passwordEncoder.encode(updateDto.getPassword()));
            }
        return usersRepository.save(user);
        }
        return null;
    }

}
