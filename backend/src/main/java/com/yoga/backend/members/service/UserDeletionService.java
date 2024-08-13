package com.yoga.backend.members.service;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDeletionService {

    private final UsersRepository usersRepository;

    public UserDeletionService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processDeletedUser(Users user) {
        log.info("사용자 삭제 처리 시작: 사용자 ID {}", user.getId());
        user.setIsDeleted(true);
        anonymizeUserData(user);
        usersRepository.save(user);
        log.info("사용자 삭제 처리 완료: 사용자 ID {}", user.getId());
    }

    private void anonymizeUserData(Users user) {
        log.debug("사용자 데이터 익명화 시작: 사용자 ID {}", user.getId());
        user.setEmail("deleted_" + user.getId() + "@yoganavi.com");
        user.setNickname("삭제된 사용자" + user.getId());
        user.setProfile_image_url(null);
        user.setProfile_image_url_small(null);
        user.setContent(null);
        user.setFcmToken(null);
        usersRepository.save(user);
        log.info("사용자 {} 익명화 완료", user.getId());
    }
}