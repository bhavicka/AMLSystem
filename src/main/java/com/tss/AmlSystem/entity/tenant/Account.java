package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.AccountStatus;
import com.tss.AmlSystem.entity.enums.tenant.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "accounts")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity {
    @Column(nullable = false, name = "client_number")
    private String clientNumber;

    @Column(nullable = false, unique = true, name = "account_number")
    private String accountNumber;

    @Column(nullable = false, name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false, name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
}
