package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private TenantRule rule;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "param_key")
    private String paramKey;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "param_value")
    private String paramValue;

    @Size(max = 255)
    @Column(name = "min_value")
    private String minValue;

    @Size(max = 255)
    @Column(name = "max_value")
    private String maxValue;
}
