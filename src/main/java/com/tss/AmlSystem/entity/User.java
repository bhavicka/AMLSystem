package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.TenantUserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_user_id")
    private SystemUser systemUser;
    @Column(nullable = false)
    private String firstName;
    private String middleName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String employeeCode;
    @Column(nullable = false)
    private TenantUserRole userRole;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private SystemUser createdBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime deactivatedAt;
}
