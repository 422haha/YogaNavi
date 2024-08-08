package com.yoga.backend.mypage.livelectures;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket 클라이언트를 테스트하는 클래스.
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */
public class TestWsClient1 {
    public static void main(String[] args) throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                // 연결 후 15초 대기한 후 OFFER 메시지 전송
                new Thread(() -> {
                    try {
                        Thread.sleep(15000);
                        session.sendMessage(new TextMessage("OFFER SDP asdaskfslkdfnlskdnfglksdnfklnsdkf"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                System.out.println("Received: " + message.getPayload());
            }
        }, "ws://127.0.0.1:8080/rtc");
    }

    /**
     * 테스트 용도로 사용되며, OFFER를 수신하면 ANSWER를 전송합니다.
     */
    public static class TestWsClient2 {
        public static void main(String[] args) throws Exception {
            StandardWebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(new TextWebSocketHandler() {
                @Override
                protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    String payload = message.getPayload();
                    System.out.println("Received: " + payload);
                    if (payload.startsWith("OFFER")) {
                        session.sendMessage(new TextMessage("ANSWER SDP saknfaslkdjflskdjfklnsdfasdasdsd"));
                    }
                }
            }, "ws://127.0.0.1:8080/rtc");
        }
    }
}
