package com.yoga.backend.config;

import com.yoga.backend.filter.CsrfCookieFilter;
import com.yoga.backend.filter.JWTTokenGeneratorFilter;
import com.yoga.backend.filter.JWTTokenValidatorFilter;
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
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class ProjectSecurityConfig {

    // 기본 보안 필터 체인을 구성하는 메서드
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 토큰 요청 처리기 설정
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        // 세션 관리 정책을 무상태(STATELESS)로 설정
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CORS 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        // 허용되는 오리진들 설정
//                        config.setAllowedOrigins(Arrays.asList("http://example1.com", "http://example2.com", "http://example3.com"));
                        // 모든 HTTP 메서드 허용
                        config.setAllowedMethods(Collections.singletonList("*"));
                        // 자격 증명(쿠키, 헤더 등) 허용
                        config.setAllowCredentials(true);
                        // 모든 헤더 허용
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        // Authorization 헤더 노출 허용
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        // 캐시 유효 시간을 1시간으로 설정
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                // CSRF 보호 비활성화
                .csrf((csrf) -> csrf.disable())
                // CSRF 토큰 요청 처리기와 무시할 요청 경로 설정
//            .csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/공용 api","/공용 api")
//            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // CSRF 쿠키 필터 추가
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                // JWT 토큰 생성 필터 추가
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                // JWT 토큰 유효성 검사 필터 추가
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                // 모든 요청에 대해 접근 허용
                .authorizeHttpRequests((requests)->requests.anyRequest().permitAll())
                // 폼 로그인 활성화
                .formLogin(Customizer.withDefaults())
                // HTTP 기본 인증 활성화
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // BCryptPasswordEncoder를 반환하는 메서드
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}