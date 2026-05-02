package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoDto {
    public String clientNumber;
    public String firstName;
    public String lastName;
    public String middleName;
    public String aadharNumber;
    public String pan;
    public String occupation;
    public String occupationType;
    public Boolean isPep;
    public Severity riskRate;
    public Double monthlyIncome;
    public String familyCode;
}
