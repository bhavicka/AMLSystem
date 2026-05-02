package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.master.MasterRuleParameter;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MasterRuleParameterRepository extends JpaRepository<MasterRuleParameter, Long> {
    List<MasterRuleParameter> findByRuleTemplate(RuleTemplate ruleTemplate);
}
