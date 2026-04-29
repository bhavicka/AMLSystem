package com.tss.AmlSystem.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RuleDashboardDto {
    List<RuleInlineDto> ruleInlineDtoList;
}
