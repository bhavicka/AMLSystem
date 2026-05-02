package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.repository.TenantRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void execute(String schemaName){
        log.info("{} Starting rule execution for tenant: {}", LogTag.RULE.getValue(), schemaName);
        TenantContext.setCurrentTenant(schemaName);
        List<TenantRule> activeRules=tenantRuleRepository.findByIsActiveTrue();
        log.info("{} Found {} active rules to execute.", LogTag.RULE.getValue(), activeRules.size());
        for(TenantRule rule:activeRules){
            log.debug("{} Delegating execution for Rule: {}", LogTag.RULE.getValue(), rule.getRuleCode());
            executionService.runRule(rule);
        }
        TenantContext.clear();
    }
}
