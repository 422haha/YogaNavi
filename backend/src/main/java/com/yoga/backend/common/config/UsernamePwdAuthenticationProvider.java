package com.yoga.backend.common.config;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UsersRepository usersRepository; // 사용자 정보를 가져오는 리포지토리

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder

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
        Optional<Users> users = usersRepository.findByEmail(username);
        if (users.isPresent()) {
            // 비밀번호 매칭 확인
            if (passwordEncoder.matches(pwd, users.get().getPwd())) {
                // 인증 성공 시 새로운 인증 객체 반환
                return new UsernamePasswordAuthenticationToken(username, pwd,
                    getGrantedAuthorities(users.get().getRole()));
            } else {
                // 비밀번호가 일치하지 않는 경우 예외 발생
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 사용자가 존재하지 않는 경우 예외 발생
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
