package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TransactionBatchProcessDto(
        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric")
        String accountNumber,
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric")
        String counterPartyAccountNumber,
        @NotBlank(message = "Transaction date is required")
        @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$", message = "Date must be dd-MM-yyyy")
        String transactionDate,
        @NotBlank(message = "Transaction type is required")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Type must be letters only")
        String transactionType,
        @NotBlank(message = "Transaction mode is required")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Mode must be letters only")
        String transactionMode,
        @NotBlank(message = "Amount is required")
        @Pattern(regexp = "^\\d{1,17}(\\.\\d{1,2})?$", message = "Invalid amount format")
        String amount,
        @NotBlank(message = "Transaction reference number is required")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Reference number must be alphanumeric")
        String transactionReferenceNumber
) {
}
