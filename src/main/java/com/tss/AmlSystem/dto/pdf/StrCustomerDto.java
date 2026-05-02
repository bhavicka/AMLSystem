package com.tss.AmlSystem.dto.pdf;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StrCustomerDto {
    public String customerNumber,
            fullName,
            aadhaarNumber,
            pan,
            occupation,
            riskRate;

    public Double monthlyIncome, professionMultiplier;
}
