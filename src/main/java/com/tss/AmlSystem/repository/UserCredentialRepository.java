package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.UserCredential;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByRefreshToken(String refreshToken);

    Optional<UserCredential> findByEmailAndPasswordHash(String email, String encode);
}
