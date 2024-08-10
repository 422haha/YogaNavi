package com.yoga.backend.members.repository;

import com.yoga.backend.common.entity.TempAuthInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TempAuthInfoRepository extends JpaRepository<TempAuthInfo, Long> {

    Optional<TempAuthInfo> findByEmail(String email);

    void deleteByEmail(String email);
}
