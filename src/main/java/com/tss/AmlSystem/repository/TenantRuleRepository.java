package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantRuleRepository extends JpaRepository<TenantRule,Long> {
    List<TenantRule> findByIsActiveTrue();

    Optional<TenantRule> findByRuleCode(String ruleCode);
    Optional<TenantRule> findByRuleName(String ruleName);
}
