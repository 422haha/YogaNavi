package com.yoga.backend.common.config;

import com.yoga.backend.common.handler.CustomAuthenticationSuccessHandler;
import com.yoga.backend.common.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;


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
        http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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
        http.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class);

        // URL 기반 권한 부여 설정
        http.authorizeHttpRequests((requests) -> requests
            .requestMatchers("/myAccount").hasRole("USER") // /myAccount 는 USER 역할 필요
            .requestMatchers("/myBalance")
            .hasAnyRole("USER", "ADMIN") // /myBalance 는 USER 또는 ADMIN 역할 필요
            .requestMatchers("/myLoans").authenticated() // /myLoans 는 인증된 사용자만 접근 가능
            .requestMatchers("/myCards").hasRole("USER") // /myCards 는 USER 역할 필요
            .requestMatchers("/user").authenticated() // /user 는 인증된 사용자만 접근 가능
            .requestMatchers("/notices", "/contact", "/register")
            .permitAll()); // 일반 사용자도 접근 가능한 URL

        // 인증 성공 시 JWT 발급
        http.formLogin(form -> form
            .successHandler(new CustomAuthenticationSuccessHandler()));

        // HTTP 기본 인증 구성
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * BCryptPasswordEncoder 를 반환
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
