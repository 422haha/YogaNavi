package com.yoga.backend.members.repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.time.LocalDateTime;
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

    Optional<Users> findById(int id);

    Optional<Users> findByIdAndIsDeletedFalse(int id);

    @Query("SELECT u FROM Users u WHERE u.email = :email AND u.isDeleted = false")
    Optional<Users> findByEmail(@Param("email") String email);

    Optional<Users> findByNickname(String nickname);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findByEmailWithLock(String email);

    List<Users> findByDeletedAtBeforeAndIsDeletedFalse(Instant dateTime);

    @Query("SELECT tl.teacher FROM TeacherLike tl WHERE tl.user.id = :userId")
    List<Users> findLikedTeachersByUserId(@Param("userId") int userId);
}
