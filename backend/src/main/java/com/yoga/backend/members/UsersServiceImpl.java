package com.yoga.backend.members;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Hashtag;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import com.yoga.backend.members.repository.HashtagRepository;
import com.yoga.backend.members.repository.UsersRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UsersServiceImpl implements UsersService {

    public static final long URL_EXPIRATION_SECONDS = 86400; // 1 hour

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JavaMailSender emailSender;

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private HashtagRepository hashtagRepository;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder,
        HashtagRepository hashtagRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.hashtagRepository = hashtagRepository;
    }

    /**
     * 회원가입
     *
     * @param registerDto 사용자 등록 정보를 담은 DTO
     * @return 등록된 사용자 엔티티
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
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
     * 닉네임의 중복 여부를 확인
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임 사용 가능 여부
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public boolean checkNickname(String nickname) {
        Optional<Users> users = usersRepository.findByNickname(nickname);
        if (users.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 이메일의 중복 여부 확인
     *
     * @param email 확인할 이메일
     * @return 이메일 사용 가능 여부
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public boolean checkUser(String email) {
        Optional<Users> users = usersRepository.findByEmail(email);
        if (users.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 인증번호 전송
     *
     * @param to      수신자 이메일
     * @param subject 메일 제목
     * @param text    메일 내용
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
     * 비밀번호 재설정 토큰을 생성하고 이메일로 전송
     *
     * @param email 사용자 이메일
     * @return 토큰 전송 결과 메시지
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
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
     * 비밀번호 재설정 토큰의 유효성 검증
     *
     * @param email 사용자 이메일
     * @param token 검증할 토큰
     * @return 토큰 유효성 여부
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean validateResetToken(String email, String token) {
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return token.equals(user.getResetToken());
        }
        return false;
    }


    /**
     * 사용자의 비밀번호 재설정
     *
     * @param email       사용자 이메일
     * @param newPassword 새 비밀번호
     * @return 비밀번호 재설정 결과 메시지
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

    /**
     * 사용자 ID로 사용자 정보 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 조회된 사용자 엔티티, 없으면 null
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Users getUserByUserId(int userId) {
        Optional<Users> users = usersRepository.findById(userId);

        if (users.isPresent()) {
            Users user = users.get();
            String profileImageUrl = user.getProfile_image_url();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                String presignedUrl = s3Service.generatePresignedUrl(profileImageUrl,
                    URL_EXPIRATION_SECONDS);
                user.setProfile_image_url(presignedUrl);
            } else {
                user.setProfile_image_url(null);
            }
            return user;
        }
        return null;
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param updateDto 업데이트할 사용자 정보를 담은 DTO
     * @param userId    업데이트할 사용자 ID
     * @return 업데이트된 사용자 엔티티, 실패 시 null
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Users updateUser(UpdateDto updateDto, int userId) {
        log.info("Updating user with ID: {}", userId);

        Optional<Users> users = usersRepository.findById(userId);

        if (users.isPresent()) {
            Users user = users.get();

            if (updateDto.getNickname() != null) {
                log.debug("Updating nickname for user {}: {}", userId, updateDto.getNickname());
                user.setNickname(updateDto.getNickname());
            }

            if (updateDto.getImageUrl() != null) {
                String oldImageUrl = user.getProfile_image_url();
                if (oldImageUrl != null && !oldImageUrl.equals(updateDto.getImageUrl())) {
                    log.debug("Deleting old profile image for user {}: {}", userId, oldImageUrl);
                    try {
                        s3Service.deleteFile(oldImageUrl);
                    } catch (Exception e) {
                        log.error("Failed to delete old profile image for user {}: {}", userId,
                            oldImageUrl, e);
                        // 여기서 예외를 던지거나 처리하는 방식을 결정해야 합니다.
                        // throw new RuntimeException("Failed to delete old profile image", e);
                    }
                }
                log.debug("Updating profile image URL for user {}: {}", userId,
                    updateDto.getImageUrl());
                user.setProfile_image_url(updateDto.getImageUrl());
            }

            if (updateDto.getPassword() != null) {
                log.debug("Updating password for user {}", userId);
                user.setPwd(passwordEncoder.encode(updateDto.getPassword()));
            }

            if (updateDto.getHashTags() != null && !updateDto.getHashTags().isEmpty()) {
                log.debug("Updating hashtags for user {}: {}", userId, updateDto.getHashTags());
                updateUserHashtags(userId, Set.copyOf(updateDto.getHashTags()));
            }
            Users updatedUser = usersRepository.save(user);
            log.info("Successfully updated user with ID: {}", userId);
            return updatedUser;

        }

        return null;
    }

    /**
     * 사용자의 해시태그 목록 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자의 해시태그 Set
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Set<String> getUserHashtags(int userId) {
        Optional<Users> users = usersRepository.findById(userId);
        if (users.isPresent()) {
            Users user = users.get();
            return user.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * 사용자의 해시태그 업데이트.
     *
     * @param userId      업데이트할 사용자 ID
     * @param newHashtags 새로운 해시태그 Set
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUserHashtags(int userId, Set<String> newHashtags) {
        Optional<Users> users = usersRepository.findById(userId);
        if (users.isPresent()) {
            Users user = users.get();

            if (user.getHashtags() == null) {
                user.setHashtags(new HashSet<>());
            }

            // 기존 해시태그 모두 제거
            user.getHashtags().clear();

            // 새로운 해시태그 추가
            for (String tagName : newHashtags) {
                Hashtag hashtag = hashtagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag();
                        newTag.setName(tagName);
                        return hashtagRepository.save(newTag);
                    });
                user.addHashtag(hashtag);
            }

            usersRepository.save(user);
        } else {
            throw new RuntimeException("사용자가 없음: " + userId);
        }
    }

}
