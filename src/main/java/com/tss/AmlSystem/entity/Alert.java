package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "alerts")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert extends BaseEntity{
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
