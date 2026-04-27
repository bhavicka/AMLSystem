package com.tss.AmlSystem.service;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.repository.TenantRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class RuleEngineService {
    private final TenantRuleRepository tenantRuleRepository;
    private final RuleExecutionService executionService;

    public void execute(){
        List<TenantRule> activeRules=tenantRuleRepository.findByIsActiveTrue();
        log.info("{} Found {} active rules to execute. Lookback Date: {}", LogTag.RULE.getValue(), activeRules.size(), lookBackDate);
        for(TenantRule rule:activeRules){
            log.debug("{} Delegating execution for Rule: {}", LogTag.RULE.getValue(), rule.getRuleCode());
            executionService.runRule(rule);
        }

    }
}
