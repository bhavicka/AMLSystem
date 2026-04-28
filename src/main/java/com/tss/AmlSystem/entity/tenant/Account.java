package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.AccountStatus;
import com.tss.AmlSystem.entity.enums.tenant.AccountType;
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

@Table(name = "accounts")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "client_number")
    private String clientNumber;

    @NotBlank
    @Size(max = 12)
    @Column(nullable = false, unique = true, name = "account_number")
    private String accountNumber;

    @NotNull
    @Column(nullable = false, name = "account_type",columnDefinition = "account_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull
    @Column(nullable = false, name = "account_status",columnDefinition = "account_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;


}
