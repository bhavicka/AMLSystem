package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.enums.OccupationType;
import com.tss.AmlSystem.entity.enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "customers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity {
    @Column(unique = true, nullable = false, name = "client_number")
    private String clientNumber;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false, unique = true, name = "aadhar_number")
    private String aadharNumber;

    @Column(nullable = false, unique = true, name = "pan")
    private String pan;

    @Column(nullable = false, name = "occupation")
    private String occupation;

    @Column(nullable = false, name = "occupation_type")
    @Enumerated(EnumType.STRING)
    private OccupationType occupationType;

    @Column(nullable = false, name = "is_pep")
    private Boolean isPep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "risk_rate")
    private Severity riskRate;

    @Column(nullable = false, precision = 19, scale = 2, name = "monthly_income")
    private BigDecimal monthlyIncome;

    @Column(nullable = false, name = "dob")
    private LocalDate dob;

    @Column(precision = 5, scale = 2, name = "profession_multiplier")
    private BigDecimal professionMultiplier;

    @Column(name = "family_code")
    private String familyCode;
}
