package com.tss.AmlSystem.entity.master;

import com.tss.AmlSystem.entity.enums.master.AuditActionType;
import com.tss.AmlSystem.entity.enums.master.GlobalUserRole;
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
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private UserCredential user;
    @Column(name = "actor_role")
    private GlobalUserRole actorRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @Column(nullable = false, name = "action_type")
    @Enumerated(EnumType.STRING)
    private AuditActionType actionType;
    @Column(name = "affected_record_id")
    private Long affectedRecordId;
    @Column(name = "affected_record_table_name")
    private String affectedRecordTableName;
    @Column(nullable = false, name = "ip_address")
    private String ipAddress;
    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}
