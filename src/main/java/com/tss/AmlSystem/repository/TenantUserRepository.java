package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    Optional<TenantUser> findByEmail(String principal);
}
