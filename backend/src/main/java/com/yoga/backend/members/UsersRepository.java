package com.yoga.backend.members;

import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.yoga.backend.common.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

    List<Users> findByEmail(String email);

    List<Users> findByNickname(String nickname);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findByEmailWithLock(String email);

//    @Transactional
//    @Modifying
//    @Query("UPDATE Users u SET u.activeToken = :newToken WHERE u.id = :userId")
//    void updateActiveToken(@Param("userId") int userId, @Param("newToken") String newToken);
}
