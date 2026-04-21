package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "cases")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Case extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String caseReferenceNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.OPEN;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;
    private LocalDateTime closedAt;
    private String notes;
}
