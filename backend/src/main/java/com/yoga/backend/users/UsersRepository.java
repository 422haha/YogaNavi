package com.yoga.backend.users;

import java.util.List;

import com.yoga.backend.common.entity.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

    List<Users> findByEmail(String email);
}
