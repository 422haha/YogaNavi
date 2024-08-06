package com.yoga.backend.members.service;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Hashtag;
import com.yoga.backend.common.entity.TempAuthInfo;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.dto.RegisterDto;
import com.yoga.backend.members.dto.UpdateDto;
import com.yoga.backend.members.repository.HashtagRepository;
import com.yoga.backend.members.repository.TempAuthInfoRepository;
import com.yoga.backend.members.repository.UsersRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UsersServiceImpl implements UsersService {

    private static final Duration TOKEN_VALIDITY_DURATION = Duration.ofMinutes(5);
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Duration DELETE_DELAY = Duration.ofDays(7);
    //    private static final Duration DELETE_DELAY = Duration.ofMinutes(2); // 시험용
    public static final long URL_EXPIRATION_SECONDS = 86400; // 1 hour

    private final S3Service s3Service;
    private final UserDeletionService userDeletionService;
    private final JavaMailSender emailSender;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashtagRepository hashtagRepository;
    private final TempAuthInfoRepository tempAuthInfoRepository;

    public UsersServiceImpl(S3Service s3Service,
        UserDeletionService userDeletionService,
        JavaMailSender emailSender,
        UsersRepository usersRepository,
        PasswordEncoder passwordEncoder,
        HashtagRepository hashtagRepository,
        TempAuthInfoRepository tempAuthInfoRepository) {
        this.s3Service = s3Service;
        this.userDeletionService = userDeletionService;
        this.emailSender = emailSender;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.hashtagRepository = hashtagRepository;
        this.tempAuthInfoRepository = tempAuthInfoRepository;
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
        String hashPwd = passwordEncoder.encode(registerDto.getPassword());

        Users user = new Users();
        user.setEmail(registerDto.getEmail());
        user.setPwd(hashPwd);
        user.setNickname(registerDto.getNickname());
        user.setIsDeleted(false);
        user.setRole(registerDto.isTeacher() ? "TEACHER" : "STUDENT");

        return usersRepository.save(user);
    }

    /**
     * 사용자 계정 복구
     *
     * @param user 사용자 엔티티
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void recoverAccount(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Users managedUser = usersRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        managedUser.setDeletedAt(null);
        managedUser.setIsDeleted(false);

        usersRepository.save(managedUser);

        log.info("사용자 계정 복구 성공. 사용자 ID: {}, 이메일: {}", managedUser.getId(), managedUser.getEmail());
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
        if (nickname.startsWith("삭제된 사용자")) {
            return false; // "삭제된 사용자"로 시작하는 닉네임은 사용 불가
        }
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
        return !usersRepository.findByEmail(email).isPresent();
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
     * 이메일 인증용 토큰을 생성하고 이메일로 전송
     *
     * @param email 사용자 이메일
     * @return 토큰 전송 결과 메시지
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String sendEmailVerificationToken(String email) {
        if (checkUser(email)) {
            String token = generateToken();
            TempAuthInfo tempAuthInfo = new TempAuthInfo();
            tempAuthInfo.setEmail(email);
            tempAuthInfo.setAuthToken(token);
            tempAuthInfo.setExpirationTime(Instant.now().plus(TOKEN_VALIDITY_DURATION));
            tempAuthInfoRepository.save(tempAuthInfo);

            sendSimpleMessage(email, "Yoga Navi 회원가입 인증번호",
                "회원가입 인증번호 : " + token + "\n이 인증번호는 5분 동안 유효합니다.");

            return "인증 번호 전송";
        } else {
            return "이미 존재하는 회원입니다.";
        }
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
            String token = generateToken();
            user.setAuthToken(token);
            user.setAuthTokenExpirationTime(Instant.now().plus(TOKEN_VALIDITY_DURATION));
            usersRepository.save(user);

            sendSimpleMessage(email, "Yoga Navi 비밀번호 재설정 인증번호",
                "비밀번호 재설정 인증번호 : " + token + "\n이 인증번호는 10분 동안 유효합니다.");

            return "인증 번호 전송";
        } else {
            return "존재하지 않는 회원입니다.";
        }
    }

    private String generateToken() {
        return Integer.toString((int) (Math.random() * 899999) + 100000);
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
    public boolean validatePasswordAuthToken(String email, String token) {
        Optional<Users> userOpt = usersRepository.findByEmailWithLock(email);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return token.equals(user.getAuthToken()) &&
                Instant.now().isBefore(user.getAuthTokenExpirationTime());
        }
        return false;
    }

    /**
     * 이메일 재설정 토큰의 유효성 검증
     *
     * @param email 사용자 이메일
     * @param token 검증할 토큰
     * @return 토큰 유효성 여부
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean validateEmailAuthToken(String email, String token) {
        Optional<TempAuthInfo> tempAuthInfoOpt = tempAuthInfoRepository.findByEmail(email);
        if (tempAuthInfoOpt.isPresent()) {
            TempAuthInfo tempAuthInfo = tempAuthInfoOpt.get();
            boolean isValid = token.equals(tempAuthInfo.getAuthToken()) &&
                Instant.now().isBefore(tempAuthInfo.getExpirationTime());
            if (isValid) {
                tempAuthInfoRepository.deleteByEmail(email);
            }
            return isValid;
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
            if (Instant.now().isAfter(user.getAuthTokenExpirationTime())) {
                return "인증 시간이 만료되었습니다. 다시 인증해주세요.";
            }
            String hashPwd = passwordEncoder.encode(newPassword);
            user.setPwd(hashPwd);
            user.setAuthToken(null);
            user.setAuthTokenExpirationTime(null);
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
        Optional<Users> users = usersRepository.findByIdAndIsDeletedFalse(userId);

        if (users.isPresent()) {
            Users user = users.get();
            String profileImageUrl = user.getProfile_image_url();
            String profileImageUrlSmall = user.getProfile_image_url_small();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                String presignedUrl = s3Service.generatePresignedUrl(profileImageUrl,
                    URL_EXPIRATION_SECONDS);
                user.setProfile_image_url(presignedUrl);
            } else {
                user.setProfile_image_url(null);
            }
            if (profileImageUrlSmall != null && !profileImageUrlSmall.isEmpty()) {
                String presignedUrlSmall = s3Service.generatePresignedUrl(profileImageUrlSmall,
                    URL_EXPIRATION_SECONDS);
                user.setProfile_image_url_small(presignedUrlSmall);
            } else {
                user.setProfile_image_url_small(null);
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
        log.info("사용자 {}의 정보 변경", userId);

        Optional<Users> users = usersRepository.findById(userId);

        if (users.isPresent()) {
            Users user = users.get();

            if (updateDto.getNickname() != null && !updateDto.getNickname().isEmpty()) {
                log.debug("사용자 {}의 닉네임 변경: {}", userId, updateDto.getNickname());
                user.setNickname(updateDto.getNickname());
            }

            if (updateDto.getImageUrl() != null && !updateDto.getImageUrl().isEmpty()) {
                String oldImageUrl = user.getProfile_image_url();
                if (oldImageUrl != null && !oldImageUrl.equals(updateDto.getImageUrl())) {
                    log.debug("사용자 {}의 프로필 이미지 삭제: {}", userId, oldImageUrl);
                    try {
                        s3Service.deleteFile(oldImageUrl);
                    } catch (Exception e) {
                        log.error("사용자 {}의 프로필 이미지 삭제 불가: {}", userId,
                            oldImageUrl, e);

                        throw new RuntimeException("이전 프로필 이미지 삭제 불가", e);
                    }
                }
                log.debug("사용자 {}의 프로필 이미지를 변경: {}", userId,
                    updateDto.getImageUrl());
                user.setProfile_image_url(updateDto.getImageUrl());
            }

            if (updateDto.getImageUrlSmall() != null && !updateDto.getImageUrlSmall().isEmpty()) {
                String oldImageUrlSmall = user.getProfile_image_url_small();
                if (oldImageUrlSmall != null && !oldImageUrlSmall.equals(
                    updateDto.getImageUrlSmall())) {
                    log.debug("사용자 {}의 소형 프로필 이미지 삭제: {}", userId, oldImageUrlSmall);
                    try {
                        s3Service.deleteFile(oldImageUrlSmall);
                    } catch (Exception e) {
                        log.error("사용자 {}의 소형 프로필 이미지 삭제 불가: {}", userId,
                            oldImageUrlSmall, e);

                        throw new RuntimeException("이전 소형 프로필 이미지 삭제 불가", e);
                    }
                }
                log.debug("사용자 {}의 소형 프로필 이미지를 변경: {}", userId,
                    updateDto.getImageUrl());
                user.setProfile_image_url_small(updateDto.getImageUrlSmall());
            }

            if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
                log.debug("사용자 {}의 비밀번호 수정", userId);
                user.setPwd(passwordEncoder.encode(updateDto.getPassword()));
            }

            if (updateDto.getHashTags() != null && !updateDto.getHashTags().isEmpty()) {
                log.debug("사용자 {}의 해시태그 수정: {}", userId, updateDto.getHashTags());
                updateUserHashtags(userId, Set.copyOf(updateDto.getHashTags()));
            }

            if (updateDto.getContent() != null && !updateDto.getContent().isEmpty()) {
                log.debug("사용자 {}의 소개 내용 수정: {}", userId, updateDto.getContent());
                user.setContent(updateDto.getContent());
            }

            Users updatedUser = usersRepository.save(user);
            log.info("사용자 {}의 정보 수정", userId);
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


    /**
     * 회원 삭제 요청
     *
     * @param userId 사용자 id
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void requestDeleteUser(int userId) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자 없음"));

        ZonedDateTime nowSeoul = ZonedDateTime.now(KOREA_ZONE_ID);
        ZonedDateTime deletionTimeSeoul = nowSeoul.plus(DELETE_DELAY);
        user.setDeletedAt(deletionTimeSeoul.toInstant());
        usersRepository.save(user);
        log.info("사용자 {} 삭제 예정: {}", userId, deletionTimeSeoul);
    }

    @Override
    @Transactional
    public void processDeletedUsers() {
        ZonedDateTime nowSeoul = ZonedDateTime.now(KOREA_ZONE_ID);
        Instant expirationTime = nowSeoul.toInstant();

        List<Users> usersToDelete = usersRepository.findByDeletedAtBeforeAndIsDeletedFalse(
            expirationTime);
        for (Users user : usersToDelete) {
            try {
                userDeletionService.processDeletedUser(user);
            } catch (Exception e) {
                log.error("사용자 {} 삭제중 에러 발생: {}", user.getId(), e);
            }
        }
    }
}
