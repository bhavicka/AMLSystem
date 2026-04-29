package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleInlineDto {
    String ruleName;
    Severity severity;
    String ruleCode;
}
