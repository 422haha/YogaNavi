package com.yoga.backend.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FCMService {

    private final UsersRepository usersRepository;

    public FCMService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void sendBatchMessagesWithData(String title, Map<String, Map<String, String>> tokenToDataMap) throws FirebaseMessagingException {
        List<Message> messages = tokenToDataMap.entrySet().stream()
            .map(entry -> Message.builder()
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(entry.getValue().get("body"))
                    .build())
                .putData("liveId", entry.getValue().get("liveId"))
                .setToken(entry.getKey())
                .build())
            .collect(Collectors.toList());

        BatchResponse response = FirebaseMessaging.getInstance().sendAll(messages);
        log.info("메시지 전송 성공 {}", response.getSuccessCount());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setNewFcm(String fcmToken, int userId) {
        Optional<Users> users = usersRepository.findById(userId);
        if (users.isPresent()) {
            Users user = users.get();
            user.setFcmToken(fcmToken);
            usersRepository.save(user);
        }
    }
}