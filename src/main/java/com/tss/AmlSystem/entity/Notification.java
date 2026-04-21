package com.tss.AmlSystem.entity;

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
public class Notification extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;
    private Long relatedRecordId;
    private String relatedRecordTableName;
    @Column(nullable = false)
    private String message;
}
