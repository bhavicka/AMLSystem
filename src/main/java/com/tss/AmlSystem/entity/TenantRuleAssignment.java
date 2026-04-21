package com.tss.AmlSystem.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Table(name = "tenant_rule_assignments",
        schema = "public",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"tenant_id", "rule_id"})
        }
        )
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantRuleAssignment extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private RuleTemplate ruleTemplate;
    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isRevoked = false;
    private LocalDateTime revokedAt;
}
