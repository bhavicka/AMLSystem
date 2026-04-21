package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRuleRepository extends JpaRepository<TenantRule,Long> {
    List<TenantRule> findByIsActiveTrue();
}
