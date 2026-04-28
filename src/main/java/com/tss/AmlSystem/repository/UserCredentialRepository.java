package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.jspecify.annotations.Nullable;
import org.mapstruct.control.MappingControl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByRefreshToken(String refreshToken);
    Optional<UserCredential> findByEmailAndPasswordHash(String email, String encode);
    List<UserCredential> findByAccountLockedAndTenantOrderByEmail(Boolean isLocked, Tenant tenant);
    List<UserCredential> findByTenant(Tenant tenant);
}
