package com.tss.AmlSystem.entity.tenant;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleVersionParameters extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_param_id")
    private TenantRuleParameter ruleParameter;

    @Column(nullable = false, name = "param_key")
    private String paramKey;

    @Column(nullable = false, name = "old_param_value")
    private String oldParamValue;

    @Column(nullable = false, name = "new_param_value")
    private String newParamValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private TenantUser changedBy;
}
