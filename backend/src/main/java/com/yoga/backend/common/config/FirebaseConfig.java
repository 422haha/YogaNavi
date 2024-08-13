package com.yoga.backend.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
        if (firebaseApps != null && !firebaseApps.isEmpty()) {
            for (FirebaseApp app : firebaseApps) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
//                    logger.info("존재하는 파이어베이스 앱 찾음");
                    return app;
                }
            }
        }

//        logger.info("파이어베이스 초기화중...");

        // 클래스패스에서 서비스 계정 키 파일을 직접 로드
        ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
//        logger.info("파이어베이스 인증 정보를 클래스패스 리소스에서 로드했습니다.");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
//        logger.info("FirebaseApp이 성공적으로 초기화되었습니다.");
        return app;
    }
}