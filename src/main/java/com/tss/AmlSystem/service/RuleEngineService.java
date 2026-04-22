package com.tss.AmlSystem.service;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.repository.TenantRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RuleEngineService {
    private final TenantRuleRepository tenantRuleRepository;
    private final RuleExecutionService executionService;

    public void execute(LocalDate lookBackDate){
        List<TenantRule> activeRules=tenantRuleRepository.findByIsActiveTrue();
//        System.out.println(activeRules);
        for(TenantRule rule:activeRules){
            executionService.runRule(rule,lookBackDate);
        }

    }
}
