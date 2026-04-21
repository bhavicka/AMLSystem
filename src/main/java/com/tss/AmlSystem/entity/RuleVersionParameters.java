package com.tss.AmlSystem.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleVersionParameters extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_param_id")
    private RuleParameter ruleParameter;
    @Column(nullable = false)
    private String paramKey;
    @Column(nullable = false)
    private String oldParamValue;
    @Column(nullable = false)
    private String newParamValue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;
}
