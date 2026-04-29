package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleDetailDto {
    public String ruleCode;
    public String ruleName;
    public String description;
    public Severity severity;
    public Map<String, String> parameters;
}
