package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.master.RuleTemplate;
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
public class TenantRule extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, unique = true)
    private RuleTemplate ruleTemplate;

    @Column(nullable = false, unique = true, name = "rule_code")
    private String ruleCode;

    @Column(nullable = false, unique = true, name = "rule_name")
    private String ruleName;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, name = "severity_rate")
    @Enumerated(EnumType.STRING)
    private Severity severityRate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
