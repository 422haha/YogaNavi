package com.yoga.backend.users;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UsersEntity, Long> {

    List<UsersEntity> findByEmail(String email);
}
