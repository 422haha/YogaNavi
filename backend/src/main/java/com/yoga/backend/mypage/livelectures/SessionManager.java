package com.yoga.backend.mypage.livelectures;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * WebRTC 세션을 관리하는 클래스.
 * 클라이언트 간의 메시지를 중계하고 세션 상태를 관리합니다.
 */
@Component
public class SessionManager extends TextWebSocketHandler {

    // 클라이언트 세션을 저장하는 맵
    private final Map<UUID, WebSocketSession> clients = new ConcurrentHashMap<>();
    // WebRTC 세션 상태를 나타내는 변수
    private WebRTCSessionState sessionState = WebRTCSessionState.Impossible;

    /**
     * 새로운 WebSocket 연결이 설정되었을 때 호출됩니다.
     *
     * @param session 새로 연결된 WebSocket 세션
     * @throws Exception 예외 발생 시
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID sessionId = UUID.randomUUID();
        if (clients.size() > 1) {
            session.close();
            return;
        }
        clients.put(sessionId, session);
        session.sendMessage(new TextMessage("Added as a client: " + sessionId));
        if (clients.size() > 1) {
            sessionState = WebRTCSessionState.Ready;
        }
        notifyAboutStateUpdate();
    }


    /**
     * 클라이언트로부터 메시지를 수신했을 때 호출됩니다.
     *
     * @param session 메시지를 보낸 WebSocket 세션
     * @param message 수신된 메시지
     * @throws Exception 예외 발생 시
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        UUID sessionId = getSessionId(session);

        // 메시지 타입에 따라 처리
        if (payload.startsWith(MessageType.STATE.toString())) {
            handleState(sessionId);
        } else if (payload.startsWith(MessageType.OFFER.toString())) {
            handleOffer(sessionId, payload);
        } else if (payload.startsWith(MessageType.ANSWER.toString())) {
            handleAnswer(sessionId, payload);
        } else if (payload.startsWith(MessageType.ICE.toString())) {
            handleIce(sessionId, payload);
        }
    }


    /**
     * WebSocket 연결이 종료되었을 때 호출됩니다.
     *
     * @param session 종료된 WebSocket 세션
     * @param status 연결 종료 상태
     * @throws Exception 예외 발생 시
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UUID sessionId = getSessionId(session);
        clients.remove(sessionId);
        sessionState = WebRTCSessionState.Impossible;
        notifyAboutStateUpdate();
    }


    /**
     * 주어진 WebSocket 세션에 해당하는 세션 ID를 반환합니다.
     *
     * @param session WebSocket 세션
     * @return 세션 ID
     */
    private UUID getSessionId(WebSocketSession session) {
        return clients.entrySet().stream()
            .filter(entry -> entry.getValue().equals(session))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Session not found"));
    }


    /**
     * 상태 메시지를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @throws Exception 예외 발생 시
     */
    private void handleState(UUID sessionId) throws Exception {
        WebSocketSession session = clients.get(sessionId);
        if (session != null) {
            session.sendMessage(new TextMessage(MessageType.STATE + " " + sessionState));
        }
    }

    /**
     * 제안 메시지를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @param message 제안 메시지
     * @throws Exception 예외 발생 시
     */
    private void handleOffer(UUID sessionId, String message) throws Exception {
        if (sessionState != WebRTCSessionState.Ready) {
            throw new IllegalStateException("Session should be in Ready state to handle offer");
        }
        sessionState = WebRTCSessionState.Creating;
        notifyAboutStateUpdate();
        WebSocketSession clientToSendOffer = clients.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Other client not found"));
        clientToSendOffer.sendMessage(new TextMessage(message));
    }

    /**
     * 응답 메시지를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @param message 응답 메시지
     * @throws Exception 예외 발생 시
     */
    private void handleAnswer(UUID sessionId, String message) throws Exception {
        if (sessionState != WebRTCSessionState.Creating) {
            throw new IllegalStateException("Session should be in Creating state to handle answer");
        }
        WebSocketSession clientToSendAnswer = clients.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Other client not found"));
        clientToSendAnswer.sendMessage(new TextMessage(message));
        sessionState = WebRTCSessionState.Active;
        notifyAboutStateUpdate();
    }

    /**
     * ICE 메시지를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @param message ICE 메시지
     * @throws Exception 예외 발생 시
     */
    private void handleIce(UUID sessionId, String message) throws Exception {
        WebSocketSession clientToSendIce = clients.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Other client not found"));
        clientToSendIce.sendMessage(new TextMessage(message));
    }

    /**
     * 상태 업데이트를 모든 클라이언트에게 알립니다.
     *
     * @throws Exception 예외 발생 시
     */
    private void notifyAboutStateUpdate() throws Exception {
        for (WebSocketSession client : clients.values()) {
            client.sendMessage(new TextMessage(MessageType.STATE + " " + sessionState));
        }
    }

    /**
     * WebRTC 세션 상태를 나타내는 열거형.
     */
    private enum WebRTCSessionState {
        Active,   // 세션이 활성 상태
        Creating, // 세션 생성 중
        Ready,    // 세션이 준비 상태
        Impossible // 세션이 불가능한 상태
    }

    /**
     * 메시지 타입을 나타내는 열거형.
     */
    private enum MessageType {
        STATE,   // 상태 메시지
        OFFER,   // 제안 메시지
        ANSWER,  // 응답 메시지
        ICE      // ICE 후보 메시지
    }
}
