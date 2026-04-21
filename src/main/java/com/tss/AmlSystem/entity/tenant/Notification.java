package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "notifications")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private TenantUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "notification_type")
    private NotificationType notificationType;

    @Column(name = "related_record_id")
    private Long relatedRecordId;

    @Column(name = "related_record_table_name")
    private String relatedRecordTableName;

    @Column(nullable = false)
    private String message;
}
