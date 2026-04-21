package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    Optional<SystemUser> findByEmail(String email);
    Optional<SystemUser> findByRefreshToken(String refreshToken);
}
