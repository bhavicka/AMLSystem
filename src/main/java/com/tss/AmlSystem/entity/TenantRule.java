package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenant_rules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRule {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private String ruleCode;
    private String ruleName;
    private String description;
    private Integer severity_rate;
}
