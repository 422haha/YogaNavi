package com.yoga.backend.livelectures;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * HttpSessionIdHandshakeInterceptor는 WebSocket 핸드셰이크 과정에서 liveId를 추출하여
 * WebSocket 세션 속성에 추가하는 인터셉터입니다.
 */
public class HttpSessionIdHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 핸드셰이크 전에 호출되며, 요청 헤더에서 liveId를 추출하여 WebSocket 세션 속성에 추가
     *
     * @param request     현재 핸드셰이크 요청
     * @param response    현재 핸드셰이크 응답
     * @param wsHandler   WebSocket 핸들러
     * @param attributes  WebSocket 세션 속성
     * @return 항상 true를 반환하여 핸드셰이크가 계속 진행되도록 합니다.
     * @throws Exception 예외가 발생할 수 있습니다.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 요청 헤더에서 liveId를 추출하여 속성으로 설정
        String liveId = request.getHeaders().getFirst("liveId");
        if (liveId != null) {
            attributes.put("liveId", liveId);
        }
        return true;
    }

    /**
     * 핸드셰이크 후에 호출되며, 현재 구현에서는 아무런 동작도 수행하지 않습니다.
     *
     * @param request   현재 핸드셰이크 요청
     * @param response  현재 핸드셰이크 응답
     * @param wsHandler WebSocket 핸들러
     * @param exception 예외가 발생할 수 있습니다.
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {
        // No-op
    }
}
