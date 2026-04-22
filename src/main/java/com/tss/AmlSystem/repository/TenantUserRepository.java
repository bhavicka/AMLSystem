package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
}
