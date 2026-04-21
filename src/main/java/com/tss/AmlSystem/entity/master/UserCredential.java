package com.tss.AmlSystem.entity.master;

import com.tss.AmlSystem.entity.enums.GlobalUserRole;
import com.tss.AmlSystem.entity.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_credentials",  schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCredential extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GlobalUserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ColumnDefault("TRUE")
    @Column(name = "is_first_login", nullable = false)
    private Boolean isFirstLogin = true;

    @ColumnDefault("TRUE")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "token_version")
    private Integer token_version;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "account_locked", nullable = false)
    @ColumnDefault("FALSE")
    private Boolean accountLocked = false;
}
