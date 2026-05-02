package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.tenant.TransactionMode;
import com.tss.AmlSystem.entity.enums.tenant.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class AlertTransactionDto {
    private String accountNumber;
    private String counterPartyAccountNumber;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private TransactionMode transactionMode;
    private BigDecimal amount;
    private String transactionReferenceNumber;
}
