package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.RuleParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleParameterRepository extends JpaRepository<RuleParameter,Long> {
    List<RuleParameter> findByRuleId(Long ruleId);
}
