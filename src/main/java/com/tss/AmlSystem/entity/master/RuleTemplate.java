package com.tss.AmlSystem.entity.master;

import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "rule_templates",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplate extends BaseEntity {
    @Column(name = "rule_code", unique = true, nullable = false)
    private String ruleCode;
    @Column(name = "rule_name", unique = true, nullable = false)
    private String ruleName;
    @Column(name = "description", nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "severity_rate", columnDefinition = "severity")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Severity severityRate;
}
