package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.enums.TenantUserRole;
import com.tss.AmlSystem.entity.master.UserCredential;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "tenant_users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantUser extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_user_id")
    private UserCredential systemUser;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true, name = "employee_code")
    private String employeeCode;

    @Column(nullable = false, name = "role")
    private TenantUserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserCredential createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;
}
