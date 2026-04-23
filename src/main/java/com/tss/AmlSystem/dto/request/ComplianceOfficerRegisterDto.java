package com.tss.AmlSystem.dto.request;

public record ComplianceOfficerRegisterDto(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String password,
        String employeeCode
){}
