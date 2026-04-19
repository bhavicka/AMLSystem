package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rule_templates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplate {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private String ruleCode;
    private String ruleName;
    private String description;
    private Integer severity_rate;
}
