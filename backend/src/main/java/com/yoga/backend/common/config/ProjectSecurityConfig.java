package com.yoga.backend.common.config;

import com.yoga.backend.common.constants.FcmConstants;
import com.yoga.backend.common.constants.SecurityConstants;
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

    private final JwtUtil jwtUtil;
    private final UsersRepository userRepository;

    public ProjectSecurityConfig(JwtUtil jwtUtil, UsersRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }


    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(jwtUtil, userRepository);
    }

    @Bean
    public JWTTokenValidatorFilter jwtTokenValidatorFilter() {
        return new JWTTokenValidatorFilter(jwtUtil, userRepository);
    }

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
                    config.setAllowedMethods(
                        Arrays.asList("GET", "POST", "PUT", "DELETE")); // HTTP 메서드 허용
                    config.setAllowCredentials(false); // Retrofit에서는  보통 필요 없음
                    config.setAllowedHeaders(Arrays.asList(
                        SecurityConstants.JWT_HEADER,
                        SecurityConstants.REFRESH_TOKEN_HEADER,
                        FcmConstants.FCM_HEADER,
                        "Content-Type"
                    ));
                    config.setExposedHeaders(Arrays.asList(
                        SecurityConstants.JWT_HEADER,
                        SecurityConstants.REFRESH_TOKEN_HEADER
                    ));
                    return config;
                }
            })
        );

        // CSRF 보호 비활성화
        http.csrf(csrf -> csrf.disable());

        // JWT 토큰 검증 필터 추가
        http.addFilterBefore(jwtTokenValidatorFilter(), BasicAuthenticationFilter.class);

        // URL 기반 권한 부여 설정
        http.authorizeHttpRequests((requests) -> requests
            // 선생만 접근 가능
            .requestMatchers("/mypage/notification/write",
                "/mypage/notification/update/**",
                "/mypage/notification/delete/**",
                "/mypage/live-lecture-manage/**",
                "/mypage/recorded-lecture/list",
                "/mypage/recorded-lecture/create",
                "/mypage/recorded-lecture/detail/**",
                "/mypage/recorded-lecture/update/**",
                "/mypage/recorded-lecture/delete").hasRole("TEACHER")

            // 모든 인증된 사용자
            .requestMatchers("/mypage/notification/list",
                "/fcm",
                "/mypage/course-history",
                "/home",
                "/mypage/info",
                "/mypage/check",
                "/mypage/update",
                "/mylogout",
                "/delete",
                "/mypage/recorded-lecture/likelist",
                "/recorded-lecture/detail/**",
                "/recorded-lecture/like/**",
                "/recorded-lecture/sort/**",
                "/recorded-lecture/search/**",
                "/teacher/**").authenticated()

            // 모두에게 열려있다!
            .requestMatchers("/members/**",
                "/is-on").permitAll()

            // 그 외의 경우
            .anyRequest().authenticated()
        );

        // 인증 성공 시 JWT 발급
        http.formLogin(form -> form
            .successHandler(customAuthenticationSuccessHandler())
            .failureHandler(new CustomLoginFailureHandler())
        );

        // HTTP 기본 인증 구성
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * BCryptPasswordEncoder 반환
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
