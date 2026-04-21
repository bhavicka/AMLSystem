package com.tss.AmlSystem.entity.global;

import com.tss.AmlSystem.entity.enums.AuditActionType;
import com.tss.AmlSystem.entity.enums.SystemUserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_audit_logs",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemAuditLog{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private SystemUser user;
    private SystemUserRole actorRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditActionType actionType;
    private Long affectedRecordId;
    private String affectedRecordTableName;
    @Column(nullable = false)
    private String ipAddress;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
