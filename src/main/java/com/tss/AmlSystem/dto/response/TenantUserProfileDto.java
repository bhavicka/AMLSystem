package com.tss.AmlSystem.dto.response;

import java.time.LocalDate;

public record TenantUserProfileDto(
        String email,
        String firstName,
        String lastName,
        String middleName,
        String employeeCode,
        LocalDate createdAt
) {
}
