package com.yoga.backend.mypage.livelectures;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


/**
 * WebSocket 설정을 위한 클래스
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionManager sessionManager;

    /**
     * WebSocketConfig 생성자
     *
     * @param sessionManager 세션 관리를 위한 매니저
     */
    public WebSocketConfig(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * WebSocket 핸들러를 등록
     *
     * @param registry WebSocket 핸들러를 등록하기 위한 레지스트리
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // /rtc 엔드포인트에 대해 sessionManager 핸들러를 추가하고, 모든 출처를 허용하며, HttpSessionIdHandshakeInterceptor를 추가
        registry.addHandler(sessionManager, "/rtc").setAllowedOrigins("*").addInterceptors(new HttpSessionIdHandshakeInterceptor());
    }
}
