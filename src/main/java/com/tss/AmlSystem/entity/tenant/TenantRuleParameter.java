package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "tenant_rule_parameters")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRuleParameter extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private TenantRule rule;

    @Column(nullable = false, name = "param_key")
    private String paramKey;

    @Column(nullable = false, name = "param_value")
    private String paramValue;

    @Column(name = "min_value")
    private String minValue;

    @Column(name = "max_value")
    private String maxValue;
}
