package com.yoga.backend.mypage.mypageMain;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class MyPageMainServiceImpl implements MyPageMainService{

    private final UsersRepository usersRepository;
    public MyPageMainServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Users getUserInfo(int userId) {
        return usersRepository.findById(userId).get(0);
    }
}
