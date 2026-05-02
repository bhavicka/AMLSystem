package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.RulePermissionDto;
import com.tss.AmlSystem.dto.request.RuleParameterUpdateDto;
import com.tss.AmlSystem.dto.response.RuleDashboardDto;
import com.tss.AmlSystem.dto.response.RuleDetailDto;
import com.tss.AmlSystem.dto.response.RuleInlineDto;
import com.tss.AmlSystem.dto.response.RuleParameterUpdatedDto;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.TenantRuleAssignment;
import com.tss.AmlSystem.entity.tenant.RuleVersionParameters;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.TenantRuleParameter;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantRuleService {

    private final TenantRuleRepository tenantRuleRepository;
    private final TenantRuleAssignmentRepository tenantRuleAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final RuleTemplateRepository ruleTemplateRepository;
    private final TenantRuleParameterRepository tenantRuleParameterRepository;
    private final RuleVersionParametersRepository ruleVersionParametersRepository;
    private final TenantUserRepository tenantUserRepository;

    public boolean assignRules(String ruleAction, RulePermissionDto rulePermissionDto){
        Boolean revoked = Boolean.TRUE, activated = Boolean.FALSE;

        if(ruleAction.equalsIgnoreCase("assign")){
            log.info("{} Assigning rules for schema: {}", LogTag.TENANT.getValue(), rulePermissionDto.schemaName());
            revoked = Boolean.FALSE;
            activated = Boolean.TRUE;
        }else {
            log.info("{} Revoking rules for schema: {}", LogTag.TENANT.getValue(), rulePermissionDto.schemaName());
        }

        Tenant tenant = tenantRepository.findBySchemaName(rulePermissionDto.schemaName())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        try {
            for (String ruleCode : rulePermissionDto.ruleCodes()) {
                RuleTemplate ruleTemplate = ruleTemplateRepository.findByRuleCode(ruleCode)
                        .orElseThrow(() -> new RuntimeException("Rule template not found for code: " + ruleCode));
                TenantRuleAssignment tenantRuleAssignment = tenantRuleAssignmentRepository
                        .findByTenantIdAndRuleTemplateId(tenant.getId(), ruleTemplate.getId())
                        .orElseGet(TenantRuleAssignment::new);
                tenantRuleAssignment.setTenant(tenant);
                tenantRuleAssignment.setRuleTemplate(ruleTemplate);
                tenantRuleAssignment.setIsRevoked(revoked);

                if (revoked) {
                    tenantRuleAssignment.setRevokedAt(LocalDateTime.now());
                } else {
                    tenantRuleAssignment.setRevokedAt(null); // Good practice to clear if un-revoked
                }

                // 4. Save (JPA will now perform an UPDATE if the ID is present)
                tenantRuleAssignmentRepository.save(tenantRuleAssignment);

            }
        }finally{
            TenantContext.clear();
        }

        try {
            TenantContext.setCurrentTenant(rulePermissionDto.schemaName());
            for(String ruleCode : rulePermissionDto.ruleCodes()){
                TenantRule tenantRule = tenantRuleRepository.findByRuleCode(ruleCode)
                        .orElseThrow(() -> new RuntimeException("Tenant rule not found for code: " + rulePermissionDto.ruleCodes().get(0)));
                tenantRule.setIsActive(activated);
                tenantRuleRepository.save(tenantRule);
                log.info("{} {} {} rule {} for schema: {}", LogTag.TENANT.getValue(), LogTag.RULE.getValue(), ruleAction, ruleCode, rulePermissionDto.schemaName());
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
                                r.getSeverityRate(),
                                r.getRuleCode())
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

    @Transactional
    public RuleParameterUpdatedDto updateRuleParameters(String ruleCode, RuleParameterUpdateDto ruleParameterUpdateDto){
        ruleCode = ruleCode.toUpperCase(Locale.ROOT);
        Map<String, String> updatedParameters = ruleParameterUpdateDto.getUpdatedParameters();

        TenantRule tenantRule = tenantRuleRepository.findByRuleCode(ruleCode).orElseThrow();
        List<TenantRuleParameter> tenantRuleParameterList = tenantRuleParameterRepository.findByRule(tenantRule);

        for(TenantRuleParameter oldParameter: tenantRuleParameterList){
            RuleVersionParameters ruleVersionParameters = new RuleVersionParameters();
            ruleVersionParameters.setRuleParameter(oldParameter);

            String paramKey = oldParameter.getParamKey();
            if(updatedParameters.containsKey(paramKey)){
                ruleVersionParameters.setParamKey(paramKey);
                Long newValue  = Long.parseLong(updatedParameters.get(paramKey));
                Long min;
                if(oldParameter.getMinValue() != null){
                    min = Long.parseLong(oldParameter.getMinValue());
                    if(newValue<min)
                        throw new IllegalArgumentException("New value of parameter is less than minimum value allowed.");
                }
                Long max;
                if(oldParameter.getMinValue() != null){
                    max = Long.parseLong(oldParameter.getMaxValue());
                    if(newValue>max)
                        throw new IllegalArgumentException("New value of parameter is greater than maximum value allowed.");
                }
                ruleVersionParameters.setOldParamValue(oldParameter.getParamValue());
                oldParameter.setParamValue(newValue.toString());
                ruleVersionParameters.setNewParamValue(oldParameter.getParamValue());

                TenantUser tenantUser = tenantUserRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow();
                ruleVersionParameters.setChangedBy(tenantUser);
                ruleVersionParametersRepository.save(ruleVersionParameters);
                tenantRuleParameterRepository.save(oldParameter);
            }
        }
        updatedParameters.clear();
        for(TenantRuleParameter parameter: tenantRuleParameterList){
            updatedParameters.put(parameter.getParamKey(), parameter.getParamValue());
        }

        return new RuleParameterUpdatedDto(ruleCode, updatedParameters);
    }

    public RuleDashboardDto getTenantRulesByBankName(String bankName, Boolean isActive){
        log.info("{} Fetching {} rules for bank: {}", LogTag.TENANT.getValue(), isActive ? "active" : "inactive", bankName);
        bankName = bankName.replace("-", " ").toUpperCase(Locale.ROOT);
        String finalBankName = bankName;
        Tenant tenant = tenantRepository.findByBankName(bankName)
                .orElseThrow(() -> new RuntimeException("Tenant not found with bank name: " + finalBankName));
        try {
            TenantContext.setCurrentTenant(tenant.getSchemaName());
            List<TenantRule> ruleList;
            if(isActive)
                ruleList = tenantRuleRepository.findByIsActiveTrue();
            else
                ruleList = tenantRuleRepository.findByIsActiveFalse();
            List<RuleInlineDto> ruleInlineDtoList = ruleList.stream()
                    .map(
                            r -> new RuleInlineDto(
                                    r.getRuleName(),
                                    r.getSeverityRate(),
                                    r.getRuleCode())
                    )
                    .toList();
            return new RuleDashboardDto(ruleInlineDtoList);
        } finally {
            TenantContext.clear();
        }
    }
}
