package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBatchProcessDto {
    private Integer rowNumber;

    @NotBlank(message = "Client number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Client number must be uppercase and numbers only")
    private String clientNumber;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must be letters only")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must be letters only")
    private String lastName;

    @Pattern(regexp = "^[A-Za-z]*$", message = "Middle name must be letters only")
    private String middleName;

    @NotBlank(message = "Aadhar number is required")
    @Pattern(regexp = "^[2-9]{1}[0-9]{11}$", message = "Aadhaar must be exactly 12 digits and cannot start with 0 or 1")
    private String aadharNumber;

    @NotBlank(message = "PAN is required")
    @Pattern(regexp = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$", message = "Invalid PAN format (Expected: 5 Letters, 4 Digits, 1 Letter)")
    private String pan;

    @NotBlank(message = "Occupation is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Occupation must be letters only")
    private String occupation;

    @NotBlank(message = "Occupation type is required")
    @Pattern(regexp = "^[A-Za-z_]+$", message = "Occupation type must be letters only")
    private String occupationType;

    @NotBlank(message = "PEP is required")
    @Pattern(
            regexp = "^(?i)(true|false)$",
            message = "isPep must be either 'true' or 'false'"
    )
    private String isPep;

    @NotBlank(message = "Risk rate is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Risk rate must be letters only")
    private String riskRate;

    @NotBlank(message = "Monthly income is required")
    @Pattern(
            regexp = "^\\d{1,17}(\\.\\d{1,2})?$",
            message = "Monthly income must be a numeric value with up to 2 decimal places"
    )
    private String monthlyIncome;

    @NotBlank(message = "Date of birth is required")
    @Pattern(
            regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|20)\\d\\d$",
            message = "DOB must be in dd-MM-yyyy format"
    )
    private String dob;

    @Pattern(
            regexp = "^([0-4](\\.\\d{1,2})?|5(\\.0{1,2})?)$",
            message = "Multiplier must be between 0.00 and 5.00"
    )
    private String professionMultiplier;

    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "Family code ")
    private String familyCode;
}

