package com.tss.AmlSystem.dto.request;

import java.math.BigInteger;

public record TransactionBatchProcessDto(
        String accountNumber,
        String counterPartyAccountNumber,
        String transactionDate,
        String transactionType,
        String transactionMode,
        String amount,
        String transactionReferenceNumber
) {
}
