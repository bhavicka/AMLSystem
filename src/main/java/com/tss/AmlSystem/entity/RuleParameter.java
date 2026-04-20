package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "rule_parameters")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleParameter extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "rule_id")
    private TenantRule rule;
    @Column(nullable = false)
    private String paramKey;
    @Column(nullable = false)
    private String paramValue;
    private String minValue;
    private String maxValue;
}
