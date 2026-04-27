package com.tss.AmlSystem.dto.request;

import lombok.Data;

public record CustomerBatchProcessDto(
        String clientNumber,
        String firstName,
        String lastName,
        String middleName,
        String aadharNumber,
        String pan,
        String occupation,
        String occupationType,
        String isPep,
        String riskRate,
        String monthlyIncome,
        String dob,
        String professionMultiplier,
        String familyCode
) {
}
