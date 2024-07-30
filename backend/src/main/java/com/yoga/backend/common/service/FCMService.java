package com.yoga.backend.common.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    private final UsersRepository usersRepository;

    public FCMService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void sendMessage(String title, String body, String email)
        throws FirebaseMessagingException {

        Optional<Users> users = usersRepository.findByEmail(email);
        if (users.isPresent()) {

            Users user = users.get();
            String token = user.getFcmToken();

            Message message = Message.builder()
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .setToken(token)
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("메시지 전송 성공 " + response);
        }

    }
}