package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Table(name = "cases")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Case extends BaseEntity {
    @Column(nullable = false, unique = true, name = "case_reference_number")
    private String caseReferenceNumber;

    @Column(nullable = false,name = "status",columnDefinition = "case_status")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private CaseStatus status = CaseStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private TenantUser assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private TenantUser assignedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    private String notes;
}
