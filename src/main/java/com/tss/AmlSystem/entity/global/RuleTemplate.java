package com.tss.AmlSystem.entity.global;

import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rule_templates",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplate extends BaseEntity {
    private String ruleCode;
    private String ruleName;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severityRate;
}
