package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<TenantUser, Long> {
}
