package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountBatchProcessDto(
        @NotBlank(message = "Client number is required")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Client number must be uppercase and numbers only")
        String clientNumber,

        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be uppercase and numbers only")
        String accountNumber,

        @NotBlank(message = "Account type is required")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Account type must be ALL CAPS with no spaces")
        String accountType,

        @Pattern(regexp = "^[A-Za-z]+$", message = "Must contain only letters and cannot be empty")
        String accountStatus
) { }