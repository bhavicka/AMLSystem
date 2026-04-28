package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.NotificationType;
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


@Table(name = "notifications")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private TenantUser user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "notification_type",columnDefinition = "notification_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NotificationType notificationType;

    @Column(name = "related_record_id")
    private Long relatedRecordId;

    @Size(max = 100)
    @Column(name = "related_record_table_name")
    private String relatedRecordTableName;

    @NotBlank
    @Column(nullable = false)
    private String message;
}
