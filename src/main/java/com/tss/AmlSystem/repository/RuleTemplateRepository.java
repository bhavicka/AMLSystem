package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.master.RuleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleTemplateRepository extends JpaRepository<RuleTemplate, Long> {
    Optional<RuleTemplate> findByRuleCode(String ruleCode);
    Optional<RuleTemplate> findByRuleName(String ruleName);
}
