package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rule_templates",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplate extends BaseEntity{
    private String ruleCode;
    private String ruleName;
    private String description;
    private Integer severity_rate;
}
