package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.TransactionMode;
import com.tss.AmlSystem.entity.enums.tenant.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "transactions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @NotBlank
    @Size(max = 12)
    @Column(nullable = false, name = "account_number")
    private String accountNumber;

    @Size(max = 12)
    @Column(name = "counter_party_account_number")
    private String counterPartyAccountNumber;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @NotNull
    @Column(nullable = false, name = "transaction_type",columnDefinition = "transaction_type")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TransactionType transactionType;

    @NotNull
    @Column(nullable = false, name = "transaction_mode",columnDefinition = "transaction_mode")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TransactionMode transactionMode;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2, name = "amount")
    private BigDecimal amount;

    @NotBlank
    @Size(max = 18)
    @Column(nullable = false, name = "transaction_reference_number")
    private String transactionReferenceNumber;
}
