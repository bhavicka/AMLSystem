package com.tss.AmlSystem.entity.master;

import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "master_rule_parameters",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterRuleParameter extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id",nullable = false)
    private RuleTemplate ruleTemplate;
    @Column(name = "param_key")
    private String paramKey;
    @Column(name = "param_value")
    private String paramValue;
    @Column(name = "min_value")
    private String minValue;
    @Column(name = "max_value")
    private String maxValue;
}
