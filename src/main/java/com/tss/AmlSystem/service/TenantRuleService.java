package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.RuleAssignmentDto;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.TenantRuleAssignment;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.repository.RuleTemplateRepository;
import com.tss.AmlSystem.repository.TenantRepository;
import com.tss.AmlSystem.repository.TenantRuleAssignmentRepository;
import com.tss.AmlSystem.repository.TenantRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantRuleService {

    private final TenantRuleRepository tenantRuleRepository;
    private final TenantRuleAssignmentRepository tenantRuleAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final RuleTemplateRepository ruleTemplateRepository;

    public boolean assignRules(RuleAssignmentDto ruleAssignmentDto){
        Tenant tenant = tenantRepository.findBySchemaName(ruleAssignmentDto.schemaName())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        for(String ruleCode : ruleAssignmentDto.ruleCodes()){
            RuleTemplate ruleTemplate = ruleTemplateRepository.findByRuleCode(ruleCode)
                    .orElseThrow(() -> new RuntimeException("Rule template not found for code: " + ruleCode));
            TenantRuleAssignment tenantRuleAssignment = new TenantRuleAssignment();
            tenantRuleAssignment.setTenant(tenant);
            tenantRuleAssignment.setRuleTemplate(ruleTemplate);
            tenantRuleAssignmentRepository.save(tenantRuleAssignment);
        }

        try {
            TenantContext.setCurrentTenant(ruleAssignmentDto.schemaName());
            for(String ruleCode : ruleAssignmentDto.ruleCodes()){
                TenantRule tenantRule = tenantRuleRepository.findByRuleCode(ruleCode)
                        .orElseThrow(() -> new RuntimeException("Tenant rule not found for code: " + ruleAssignmentDto.ruleCodes().get(0)));
                tenantRule.setIsActive(true);
                tenantRuleRepository.save(tenantRule);
            }
        } finally {
            TenantContext.clear();
        }
        return true;
    }
}
