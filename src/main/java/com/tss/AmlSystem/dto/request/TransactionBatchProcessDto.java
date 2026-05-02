package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionBatchProcessDto {
    private Integer rowNumber;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be alphanumeric")
    private String accountNumber;

    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Account number must be alphanumeric")
    private String counterPartyAccountNumber;

    @NotBlank(message = "Transaction date is required")
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$", message = "Date must be dd-MM-yyyy")
    private String transactionDate;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^[A-Za-z_]+$", message = "Type must be letters only")
    private String transactionType;

    @NotBlank(message = "Transaction mode is required")
    @Pattern(regexp = "^[A-Za-z_]+$", message = "Mode must be letters only")
    private String transactionMode;

    @NotBlank(message = "Amount is required")
    @Pattern(regexp = "^\\d{1,17}(\\.\\d{1,2})?$", message = "Invalid amount format")
    private String amount;

    @NotBlank(message = "Transaction reference number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Reference number must be alphanumeric")
    private String transactionReferenceNumber;
}

