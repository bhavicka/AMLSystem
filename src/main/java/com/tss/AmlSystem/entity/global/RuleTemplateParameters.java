package com.tss.AmlSystem.entity.global;

import com.tss.AmlSystem.entity.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rule_template_parameters",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplateParameters extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id",nullable = false)
    private RuleTemplate ruleTemplate;

    private String paramKey;
    private String paramValue;

    private String minValue;
    private String maxValue;
}
