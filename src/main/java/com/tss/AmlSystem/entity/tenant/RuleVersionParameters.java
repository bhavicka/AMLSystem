package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "rule_version_parameters")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleVersionParameters extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_param_id")
    private TenantRuleParameter ruleParameter;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "param_key")
    private String paramKey;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "old_param_value")
    private String oldParamValue;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "new_param_value")
    private String newParamValue;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private TenantUser changedBy;
}
