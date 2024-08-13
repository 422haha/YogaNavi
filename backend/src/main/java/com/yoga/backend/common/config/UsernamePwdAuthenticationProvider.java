package com.yoga.backend.common.config;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.members.service.UsersService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(
        UsernamePwdAuthenticationProvider.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersService usersService;

    public UsernamePwdAuthenticationProvider(UsersRepository usersRepository,
        PasswordEncoder passwordEncoder,
        UsersService usersService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersService = usersService;
    }// BCryptPasswordEncoder

    /**
     * 사용자 인증을 수행하는 메서드
     *
     * @param authentication 인증 정보
     * @return 인증 성공 시 새로운 인증 객체
     * @throws AuthenticationException 인증 실패 시 예외 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        // 사용자 이름과 비밀번호 추출
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        // 사용자 정보 조회
        Optional<Users> userOpt = usersRepository.findByEmail(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();

            if (user.getIsDeleted()) {
                log.warn("삭제된 계정으로 로그인 시도: {}", username);
                throw new BadCredentialsException("계정이 삭제되었습니다.");
            }

            // 탈퇴 진행 중인 사용자 확인
            if (user.getDeletedAt() != null) {
                // 비밀번호 확인
                if (passwordEncoder.matches(pwd, user.getPwd())) {
                    usersService.recoverAccount(user);

                    user = usersRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("User not found after recovery"));

                    log.info("사용자 계정이 복구됨. 사용자 email: {}", user.getEmail());
                } else {
                    log.warn("탈퇴 진행 중인 계정 비밀번호 불일치: {}", username);
                    throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
                }
            }

            if (passwordEncoder.matches(pwd, user.getPwd())) {
                log.info("사용자 인증 성공: {}", username);
                return new UsernamePasswordAuthenticationToken(username, pwd,
                    getGrantedAuthorities(user.getRole()));
            } else {
                log.warn("비밀번호 불일치: {}", username);
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            log.warn("존재하지 않는 사용자로 로그인 시도: {}", username);
            throw new BadCredentialsException("사용자가 존재하지 않습니다.");
        }
    }

    /**
     * 사용자 권한 목록 생성
     *
     * @param role 사용자 역할
     * @return 사용자 권한
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        return grantedAuthorities;
    }

    /**
     * 이 인증 제공자가 지원하는 인증 유형 확인
     *
     * @param authentication 인증 유형
     * @return 지원 여부
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
