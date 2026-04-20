package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "tenant_rules")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRule extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false, unique = true)
    private RuleTemplate ruleTemplate;
    @Column(nullable = false, unique = true)
    private String ruleCode;
    @Column(nullable = false, unique = true)
    private String ruleName;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private Integer severityRate;
    @Column(nullable = false)
    private Boolean isActive;
}
