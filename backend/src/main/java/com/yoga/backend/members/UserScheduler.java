package com.yoga.backend.members;

import com.yoga.backend.members.service.UsersService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserScheduler {

    private final UsersService usersService;

    public UserScheduler(UsersService usersService) {
        this.usersService = usersService;
    }

    //    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
    @Scheduled(cron = "0 * * * * *") // 테스트용. 1분마다 실행
    public void processDeletedUsers() {
        usersService.processDeletedUsers();
    }
}