package com.yoga.backend.fcm;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /**
     * @param title          메시지 제목
     * @param tokenToDataMap 메시지 내용 및
     */
    public void sendBatchMessagesWithData(String title,
        Map<String, Map<String, String>> tokenToDataMap) throws FirebaseMessagingException {

        List<Message> messages = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> map : tokenToDataMap.entrySet()) {
            String token = map.getKey();
            Map<String, String> data = map.getValue();

            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(data.get("body"))
                .build();

            Message message = Message.builder()
                .setNotification(notification)
                .putData("liveId", data.get("liveId"))
                .setToken(token)
                .build();

            messages.add(message);
        }

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