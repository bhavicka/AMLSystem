package com.tss.AmlSystem.dto.request;

import lombok.Data;

@Data
public class CustomerBatchProcessDto {
    private String clientNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String aadharNumber;
    private String pan;
    private String occupation;
    private String occupationType;
    private String isPep;
    private String riskRate;
    private String monthlyIncome;
    private String dob;
    private String professionMultiplier;
    private String familyCode;
}
