package com.yoga.backend.common.config;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.users.UsersRepository;
import java.util.ArrayList;
import java.util.List;
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
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        System.out.println("======= username: " + username + " pwd: " + pwd);
        List<Users> users = usersRepository.findByEmail(username);
        System.out.println("======= size  : " + users.size());
        if (users.size() > 0) {
            if (passwordEncoder.matches(pwd, users.get(0).getPwd())) {
                System.out.println(
                    "=====================sdfaf:" + new UsernamePasswordAuthenticationToken(
                        username, pwd,
                        getGrantedAuthorities(users.get(0).getRole())));
                return new UsernamePasswordAuthenticationToken(username, pwd,
                    getGrantedAuthorities(users.get(0).getRole()));
            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        } else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        System.out.println("grantedAuthorities : " + grantedAuthorities);
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
