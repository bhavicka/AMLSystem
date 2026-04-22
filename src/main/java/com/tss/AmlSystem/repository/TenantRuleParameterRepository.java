package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.TenantRuleParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRuleParameterRepository extends JpaRepository<TenantRuleParameter,Long> {
    List<TenantRuleParameter> findByRuleId(Long ruleId);
}
