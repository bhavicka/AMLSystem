package com.tss.AmlSystem.entity.master;

import com.tss.AmlSystem.entity.enums.master.GlobalUserRole;
import com.tss.AmlSystem.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(nullable = false, columnDefinition = "global_user_role_enum")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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
