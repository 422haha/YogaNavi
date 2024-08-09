package com.yoga.backend.livelectures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 연결 관리를 위한 클래스
 */
@Component
public class SessionManager extends TextWebSocketHandler {

    private final Map<String, Map<UUID, WebSocketSession>> liveLectureSessions = new ConcurrentHashMap<>();
    private final Map<String, WebRTCSessionState> roomStates = new ConcurrentHashMap<>();
    private final Map<UUID, ClientState> clientStates = new ConcurrentHashMap<>();

    @Autowired
    private LiveLectureService liveLectureService;

    /**
     * WebSocket 연결이 설정된 후 호출됨
     *
     * @param session WebSocket 세션
     * @throws Exception 예외 발생 시
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String liveId = getLiveIdFromHeaders(session);
        UUID sessionId = UUID.randomUUID();

        System.out.println("연결 설정됨 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId);

        liveLectureSessions.putIfAbsent(liveId, new ConcurrentHashMap<>());
        Map<UUID, WebSocketSession> clients = liveLectureSessions.get(liveId);
        if (clients.size() >= 2) {
            System.out.println("연결 종료 - 이미 두 개의 클라이언트가 연결되어 있음.");

            session.close();
            return;
        }

        clients.put(sessionId, session);
        clientStates.put(sessionId, ClientState.CONNECTED);

        session.sendMessage(new TextMessage("Added as a client: " + sessionId));
        updateRoomState(liveId);
    }

    /**
     * 텍스트 메시지를 처리함
     *
     * @param session WebSocket 세션
     * @param message 수신된 메시지
     * @throws Exception 예외 발생 시
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String liveId = getLiveIdFromHeaders(session);
        String payload = message.getPayload();
        UUID sessionId = getSessionId(session, liveId);

        System.out.println("메시지 수신 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId + ", 메시지: " + payload);

        if (payload.startsWith(MessageType.STATE.toString())) {
            handleState(sessionId, liveId);
        } else if (payload.startsWith(MessageType.OFFER.toString())) {
            handleOffer(sessionId, payload, liveId);
        } else if (payload.startsWith(MessageType.ANSWER.toString())) {
            handleAnswer(sessionId, payload, liveId);
        } else if (payload.startsWith(MessageType.ICE.toString())) {
            handleIce(sessionId, payload, liveId);
        }
    }

    /**
     * WebSocket 연결이 종료된 후 호출됨
     *
     * @param session WebSocket 세션
     * @param status 연결 종료 상태
     * @throws Exception 예외 발생 시
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String liveId = getLiveIdFromHeaders(session);
        UUID sessionId = getSessionId(session, liveId);

        System.out.println("연결 종료됨 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId);

        Map<UUID, WebSocketSession> clients = liveLectureSessions.get(liveId);
        if (clients != null) {
            clients.remove(sessionId);
            clientStates.remove(sessionId);
            if (clients.isEmpty()) {
                liveLectureSessions.remove(liveId);
                roomStates.remove(liveId);
                System.out.println("라이브 ID: " + liveId + "에 대한 모든 클라이언트 연결이 종료됨");
            } else {
                updateRoomState(liveId);
            }
        }
    }

    /**
     * WebSocket 세션에서 liveId를 가져옴
     *
     * @param session WebSocket 세션
     * @return liveId
     */
    private String getLiveIdFromHeaders(WebSocketSession session) {
        String liveId = session.getAttributes().get("liveId").toString();
        if (liveId == null) {
            throw new IllegalStateException("웹 소켓 요청에 liveId 헤더가 누락되었습니다.");
        }
        return liveId;
    }

    /**
     * WebSocket 세션에서 sessionId를 가져옴
     *
     * @param session WebSocket 세션
     * @param liveId 라이브 ID
     * @return sessionId
     */
    private UUID getSessionId(WebSocketSession session, String liveId) {
        Map<UUID, WebSocketSession> clients = liveLectureSessions.get(liveId);
        for (Map.Entry<UUID, WebSocketSession> entry : clients.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("세션을 찾을 수 없습니다.");
    }

    /**
     * 상태 메시지를 처리
     *
     * @param sessionId 세션 ID
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void handleState(UUID sessionId, String liveId) throws IOException {
        WebSocketSession session = liveLectureSessions.get(liveId).get(sessionId);
        if (session != null) {
            System.out.println("상태 처리 - 세션 ID: " + sessionId + ", 상태: " + roomStates.get(liveId));
            session.sendMessage(new TextMessage(MessageType.STATE + " " + roomStates.get(liveId)));
        }
    }

    /**
     * 오퍼 메시지를 처리
     *
     * @param sessionId 세션 ID
     * @param message 수신된 메시지
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void handleOffer(UUID sessionId, String message, String liveId) throws IOException {
        if (roomStates.get(liveId) != WebRTCSessionState.Ready) {
            return;
        }
        roomStates.put(liveId, WebRTCSessionState.Creating);
        notifyAboutStateUpdate(liveId);

        WebSocketSession clientToSendOffer = liveLectureSessions.get(liveId).entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("다른 클라이언트를 찾을 수 없습니다."));

        System.out.println("제안 처리 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId + ", 메시지: " + message);
        clientToSendOffer.sendMessage(new TextMessage(message));
        clientStates.put(sessionId, ClientState.OFFER_SENT);

        // 응답에 대한 타임아웃 설정
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (roomStates.get(liveId) == WebRTCSessionState.Creating) {
                    try {
                        System.out.println("응답 시간 초과 - 라이브 ID: " + liveId);
                        resetRoom(liveId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 10000); // 10초 타임아웃
    }

    /**
     * 응답 메시지를 처리
     *
     * @param sessionId 세션 ID
     * @param message 수신된 메시지
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void handleAnswer(UUID sessionId, String message, String liveId) throws IOException {
        if (roomStates.get(liveId) != WebRTCSessionState.Creating) {
            return;
        }

        WebSocketSession clientToSendAnswer = liveLectureSessions.get(liveId).entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("다른 클라이언트를 못 찾았습니다."));

        System.out.println("응답 처리 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId + ", 메시지: " + message);
        clientToSendAnswer.sendMessage(new TextMessage(message));
        roomStates.put(liveId, WebRTCSessionState.Active);
        clientStates.put(sessionId, ClientState.CONNECTED);
        notifyAboutStateUpdate(liveId);
    }

    /**
     * ICE 후보 메시지를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @param message 수신된 메시지
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void handleIce(UUID sessionId, String message, String liveId) throws IOException {
        WebSocketSession clientToSendIce = liveLectureSessions.get(liveId).entrySet().stream()
            .filter(entry -> !entry.getKey().equals(sessionId))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("O다른 클라이언트를 못 찾았습니다."));

        System.out.println("ICE 후보 처리 - 세션 ID: " + sessionId + ", 라이브 ID: " + liveId + ", 메시지: " + message);
        clientToSendIce.sendMessage(new TextMessage(message));
    }

    // 비동기 처리
//    private void updateRoomState(String liveId) throws IOException {
//        int roomSize = liveLectureSessions.get(liveId).size();
//        roomStates.put(liveId, roomSize == 2 ? WebRTCSessionState.Ready : WebRTCSessionState.Impossible);
//        System.out.println("방 상태 업데이트 - 라이브 ID: " + liveId + ", 상태: " + roomStates.get(liveId));
//        liveLectureService.updateIsOnAir(Long.valueOf(liveId), roomSize > 0).thenRun(() -> {
//            System.out.println("isOnAir 업데이트 완료 - 라이브 ID: " + liveId + ", isOnAir: " + (roomSize > 0));
//        }).exceptionally(ex -> {
//            ex.printStackTrace();
//            return null;
//        });
//        notifyAboutStateUpdate(liveId);
//    }

    /**
     * 방 상태를 업데이트
     *
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
private void updateRoomState(String liveId) throws IOException {
    int roomSize = liveLectureSessions.get(liveId).size();
    roomStates.put(liveId, roomSize == 2 ? WebRTCSessionState.Ready : WebRTCSessionState.Impossible);
    System.out.println("방 상태 업데이트 - 라이브 ID: " + liveId + ", 상태: " + roomStates.get(liveId));
    liveLectureService.updateIsOnAir(Long.valueOf(liveId), roomSize > 0);
    notifyAboutStateUpdate(liveId);
}

    /**
     * 방을 초기화
     *
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void resetRoom(String liveId) throws IOException {
        roomStates.put(liveId, WebRTCSessionState.Impossible);
        liveLectureSessions.get(liveId).forEach((sessionId, session) -> {
            clientStates.put(sessionId, ClientState.CONNECTED);
        });
        System.out.println("방 초기화 - 라이브 ID: " + liveId);
        updateRoomState(liveId);
    }

    /**
     * 방 상태 업데이트 알림
     *
     * @param liveId 라이브 ID
     * @throws IOException 예외 발생 시
     */
    private void notifyAboutStateUpdate(String liveId) throws IOException {
        WebRTCSessionState state = roomStates.getOrDefault(liveId, WebRTCSessionState.Impossible);
        System.out.println("상태 업데이트 알림 - 라이브 ID: " + liveId + ", 상태: " + state);
        for (WebSocketSession client : liveLectureSessions.get(liveId).values()) {
            client.sendMessage(new TextMessage(MessageType.STATE + " " + state));
        }
    }

    // WebRTC 세션 상태 열거형
    private enum WebRTCSessionState {
        Active, Creating, Ready, Impossible
    }

    // 클라이언트 상태 열거형
    private enum ClientState {
        CONNECTED, OFFER_SENT
    }

    // 메시지 유형 열거형
    private enum MessageType {
        STATE, OFFER, ANSWER, ICE
    }
}
