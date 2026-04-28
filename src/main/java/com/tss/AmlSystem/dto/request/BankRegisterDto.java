package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.*;

public record BankRegisterDto(
        @NotBlank(message = "Bank name is required")
        @Size(max = 100, message = "Bank name must be under 100 characters")
        String bankName,

        @NotBlank(message = "IFSC code is required")
        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC format (e.g., ABCD0123456)")
        String ifsc,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Invalid contact email format")
        String contactEmail,

        @NotBlank(message = "Bank admin email is required")
        @Email(message = "Invalid admin email format")
        String bankAdminEmail,

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(max = 50, message = "Middle name must be under 50 characters")
        String middleName, // Optional, so no @NotBlank

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,

        @NotBlank(message = "Employee code is required")
        @Pattern(regexp = "^[a-zA-Z0-9-]{3,20}$", message = "Employee code must be alphanumeric (3-20 characters)")
        String employeeCode
) {}
