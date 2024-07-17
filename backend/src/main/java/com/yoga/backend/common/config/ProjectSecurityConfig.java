package com.yoga.backend.common.config;

import com.yoga.backend.common.handler.CustomAuthenticationSuccessHandler;
import com.yoga.backend.common.filter.JWTTokenValidatorFilter;
import com.yoga.backend.common.handler.CustomLoginFailureHandler;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
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
            .requestMatchers("/myAccount").hasRole("USER")
            .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/myLoans").authenticated()
            .requestMatchers("/myCards").hasRole("USER")
            .requestMatchers("/user").authenticated()
            .requestMatchers("/members/**","/fffff/ff") // 추가된 부분
            .permitAll()); // /members/register/** 엔드포인트에 대한 인증 제거

        // 인증 성공 시 JWT 발급
        http.formLogin(form -> form
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(new CustomLoginFailureHandler())
//            .loginPage("/login")
//            .failureUrl("/login-fail")
        );

//        http.sessionManagement(session -> session
//            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//            .maximumSessions(1)
//            .maxSessionsPreventsLogin(false)
//            .expiredSessionStrategy(event -> {
//                HttpServletResponse response = event.getResponse();
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().print("다른 사용자가 로그인 하여 이 세션은 만료됩니다.");
//                response.flushBuffer();
//            }));

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
