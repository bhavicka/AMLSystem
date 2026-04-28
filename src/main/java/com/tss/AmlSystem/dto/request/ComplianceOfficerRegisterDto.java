package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.*;

public record ComplianceOfficerRegisterDto(
        @NotBlank(message = "First name is required")
        @Size(max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        String lastName,

        @Size(max = 50)
        String middleName, // Optional

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Employee code is required")
        @Size(min = 3, max = 20)
        String employeeCode
){}
