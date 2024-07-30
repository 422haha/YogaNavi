package com.yoga.backend.common.config;

import com.yoga.backend.common.handler.CustomAuthenticationSuccessHandler;
import com.yoga.backend.common.filter.JWTTokenValidatorFilter;
import com.yoga.backend.common.handler.CustomLoginFailureHandler;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
public class ProjectSecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersRepository userRepository;


    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(jwtUtil, userRepository);
    }

    @Bean
    public JWTTokenValidatorFilter jwtTokenValidatorFilter() {
        return new JWTTokenValidatorFilter(jwtUtil);
    }

//    @Bean FcmFilter fcmFilter() {
//        return new FcmFilter(jwtUtil, userRepository);
//    }

    /**
     * 기본 보안 필터 체인을 구성
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 예외 처리
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        // 세션 관리 설정: stateless
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CORS 구성
        http.cors(
            corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("*")); // 모든 출처 허용
                    config.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
                    config.setAllowCredentials(true); // 자격 증명 포함 요청 허용
                    config.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                    config.setExposedHeaders(Arrays.asList("Authorization")); // Authorization 헤더 노출
                    config.setMaxAge(3600L); // 캐싱 시간 설정 (1시간)
                    return config;
                }
            }));

        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());

        // JWT 토큰 검증 필터 추가
        http.addFilterBefore(jwtTokenValidatorFilter(), BasicAuthenticationFilter.class);

        // URL 기반 권한 부여 설정
        http.authorizeHttpRequests((requests) -> requests
                .anyRequest().permitAll()
//            .requestMatchers("/myAccount").hasRole("USER")
//            .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
//            .requestMatchers("/test").authenticated()
//            .requestMatchers("/mypage/recorded-lecture/").hasRole("TEACHER")
//            .requestMatchers("/user").authenticated()
//                .requestMatchers("/members/**").permitAll()
//                .requestMatchers("/test").authenticated()

        ); // /members/register/** 엔드포인트에 대한 인증 제거

        // 인증 성공 시 JWT 발급
        http.formLogin(form -> form
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(new CustomLoginFailureHandler())
//            .loginPage("/login")
//            .failureUrl("/login-fail")
        );

        // HTTP 기본 인증 구성
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * BCryptPasswordEncoder 를 반환
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
