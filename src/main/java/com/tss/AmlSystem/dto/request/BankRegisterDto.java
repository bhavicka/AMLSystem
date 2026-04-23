package com.tss.AmlSystem.dto.request;

public record  BankRegisterDto (
        String bankName,
        String ifsc,
        String contactEmail,
        String bankAdminEmail,
        String password,
        String firstName,
        String middleName,
        String lastName,
        String employeeCode
){}
