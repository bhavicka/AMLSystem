package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.TransactionMode;
import com.tss.AmlSystem.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "transactions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
    @Column(nullable = false)
    private String accountNumber;
    private String counterPartyAccountNumber;
    private LocalDate transactionDate;
    @Column(nullable = false)
    private TransactionType transactionType;
    @Column(nullable = false)
    private TransactionMode transactionMode;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String transactionReferenceNumber;
}
