package com.yoga.backend.common.config;

import com.yoga.backend.common.filter.CsrfCookieFilter;
import com.yoga.backend.common.filter.JWTTokenGeneratorFilter;
import com.yoga.backend.common.filter.JWTTokenValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class ProjectSecurityConfig {

    // @Bean
    // SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    //     CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    //     requestHandler.setCsrfRequestAttributeName("_csrf");
    //     System.out.println("====================defaultSecurityFilterChain");
    //     http.sessionManagement(
    //             session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .cors(
    //             corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
    //                 @Override
    //                 public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
    //                     CorsConfiguration config = new CorsConfiguration();
    //                     // 모든 출처 허용
    //                     System.out.println("====================sessionManagement");
    //                     config.setAllowedOrigins(Collections.singletonList("*"));
    //                     config.setAllowedMethods(Collections.singletonList("*"));
    //                     config.setAllowCredentials(true);
    //                     config.setAllowedHeaders(Collections.singletonList("*"));
    //                     config.setExposedHeaders(Arrays.asList("Authorization"));
    //                     config.setMaxAge(3600L);
    //                     return config;
    //                 }
    //             }))
    //         .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler)
    //             .ignoringRequestMatchers("/api/**")
    //             .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
    //         .authorizeHttpRequests((requests) -> requests
    //             .requestMatchers("/api/**").permitAll()
    //             .anyRequest().authenticated())
    //         .formLogin(Customizer.withDefaults())
    //         .httpBasic(Customizer.withDefaults());
    //     return http.build();
    // }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        System.out.println("====================defaultSecurityFilterChain");
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(
                corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        // 모든 출처 허용
                        System.out.println("====================sessionManagement");
                        config.setAllowedOrigins(Collections.singletonList("*"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
            .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests((requests) -> requests
                .anyRequest().permitAll())
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
