package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "rule_parameters",schema = "sbi_1776780613098")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleParameter extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private TenantRule rule;
    @Column(nullable = false)
    private String paramKey;
    @Column(nullable = false)
    private String paramValue;
    private String minValue;
    private String maxValue;
}
