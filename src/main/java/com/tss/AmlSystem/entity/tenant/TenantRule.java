package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.master.RuleTemplate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Table(name = "tenant_rules")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRule extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false, unique = true)
    private RuleTemplate ruleTemplate;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true, name = "rule_code")
    private String ruleCode;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true, name = "rule_name")
    private String ruleName;

    @NotBlank
    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(nullable = false, name = "severity_rate",columnDefinition = "severity")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    private Severity severityRate;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
