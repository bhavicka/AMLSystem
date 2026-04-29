package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.RuleAssignmentDto;
import com.tss.AmlSystem.dto.response.RuleDashboardDto;
import com.tss.AmlSystem.dto.response.RuleDetailDto;
import com.tss.AmlSystem.dto.response.RuleInlineDto;
import com.tss.AmlSystem.entity.master.MasterRuleParameter;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.TenantRuleAssignment;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.TenantRuleParameter;
import com.tss.AmlSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantRuleService {

    private final TenantRuleRepository tenantRuleRepository;
    private final TenantRuleAssignmentRepository tenantRuleAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final RuleTemplateRepository ruleTemplateRepository;
    private final TenantRuleParameterRepository tenantRuleParameterRepository;

    public boolean assignRules(RuleAssignmentDto ruleAssignmentDto){
        log.info("{} Assigning rules for schema: {}", LogTag.TENANT.getValue(), ruleAssignmentDto.schemaName());
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
                log.info("{} {} Activated rule {} for schema: {}", LogTag.TENANT.getValue(), LogTag.RULE.getValue(), ruleCode, ruleAssignmentDto.schemaName());
            }
        } finally {
            TenantContext.clear();
        }
        return true;
    }

    public RuleDashboardDto getTenantRuleList(){
        List<TenantRule> ruleList = tenantRuleRepository.findByIsActiveTrue();
        List<RuleInlineDto> ruleInlineDtoList = ruleList.stream()
                .map(
                        r -> new RuleInlineDto(
                                r.getRuleName(),
                                r.getSeverityRate())
                )
                .toList();
        return new RuleDashboardDto(ruleInlineDtoList);
    }


    public RuleDetailDto getRuleDetails(String ruleCode){
        ruleCode = ruleCode.toUpperCase(Locale.ROOT);
        String finalRuleCode = ruleCode;
        TenantRule tenantRule = tenantRuleRepository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new RuntimeException("Rule not found with code: " + finalRuleCode));
        RuleDetailDto ruleDetailDto = new RuleDetailDto();
        ruleDetailDto.setRuleCode(tenantRule.getRuleCode());
        ruleDetailDto.setRuleName(tenantRule.getRuleName());
        ruleDetailDto.setDescription(tenantRule.getDescription());
        ruleDetailDto.setSeverity(tenantRule.getSeverityRate());
        List<TenantRuleParameter> tenantRuleParameterList = tenantRuleParameterRepository
                .findByRule(tenantRule);
        Map<String, String> parameters = new HashMap<>();
        for(TenantRuleParameter parameter: tenantRuleParameterList){
            parameters.put(parameter.getParamKey(), parameter.getParamValue());
        }
        ruleDetailDto.setParameters(parameters);
        return ruleDetailDto;
    }
}
