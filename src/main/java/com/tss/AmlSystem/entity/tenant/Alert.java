package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
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

import java.util.List;

@Table(name = "alerts")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private TenantRule tenantRule;
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case caseId;

    @NotBlank
    @Column(name = "alert_number", nullable = false, unique = true)
    private String alertNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // This tells Hibernate to use the DB's native enum
    @Column(name = "status", nullable = false, columnDefinition = "alert_status")
    private AlertStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "transaction_alerts",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private List<Transaction> transactions;

    @NotBlank
    @Size(max = 255)
    @Column(name = "client_number",nullable = false)
    private String clientNumber;

    @NotBlank
    @Size(max = 64)
    @Column(name = "alert_hash",nullable = false,unique = true)
    private String alertHash;
}
