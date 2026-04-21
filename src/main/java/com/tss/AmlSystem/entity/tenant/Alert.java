package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "alerts",schema = "sbi_1776780613098")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private TenantRule tenantRule;
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case caseId;
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "transaction_alerts",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private List<Transaction> transactions;
}
