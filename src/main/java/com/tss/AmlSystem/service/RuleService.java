package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.RuleDashboardDto;
import com.tss.AmlSystem.dto.response.RuleDetailDto;
import com.tss.AmlSystem.dto.response.RuleInlineDto;
import com.tss.AmlSystem.entity.master.MasterRuleParameter;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import com.tss.AmlSystem.repository.MasterRuleParameterRepository;
import com.tss.AmlSystem.repository.RuleTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleService {
    private final RuleTemplateRepository ruleTemplateRepository;
    private final MasterRuleParameterRepository masterRuleParameterRepository;

    public RuleDashboardDto getMasterRulesList(){
        List<RuleTemplate> ruleList = ruleTemplateRepository.findAll();
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
        RuleTemplate ruleTemplate = ruleTemplateRepository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new RuntimeException("Rule not found with code: " + finalRuleCode));
        RuleDetailDto ruleDetailDto = new RuleDetailDto();
        ruleDetailDto.setRuleCode(ruleTemplate.getRuleCode());
        ruleDetailDto.setRuleName(ruleTemplate.getRuleName());
        ruleDetailDto.setDescription(ruleTemplate.getDescription());
        ruleDetailDto.setSeverity(ruleTemplate.getSeverityRate());
        List<MasterRuleParameter> masterRuleParameterList = masterRuleParameterRepository
                .findByRuleTemplate(ruleTemplate);
        Map<String, String> parameters = new HashMap<>();
        for(MasterRuleParameter parameter: masterRuleParameterList){
            parameters.put(parameter.getParamKey(), parameter.getParamValue());
        }
        ruleDetailDto.setParameters(parameters);
        return ruleDetailDto;
    }
}
