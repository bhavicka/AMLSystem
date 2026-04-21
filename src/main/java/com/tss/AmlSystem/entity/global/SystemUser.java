package com.tss.AmlSystem.entity.global;

import com.tss.AmlSystem.entity.enums.SystemUserRole;
import com.tss.AmlSystem.entity.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_users",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemUser extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemUserRole role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ColumnDefault("TRUE")
    private Boolean isFirstLogin = true;
    @ColumnDefault("TRUE")
    private Boolean isActive = true;
    private LocalDateTime lastLoginAt;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;
    private Integer token_version;
    private Integer failedLoginAttempts;
    @ColumnDefault("FALSE")
    private Boolean accountLocked = false;
}
