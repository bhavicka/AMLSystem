package com.tss.AmlSystem.entity.tenant;

import com.tss.AmlSystem.entity.BaseEntity;
import com.tss.AmlSystem.entity.enums.tenant.OccupationType;
import com.tss.AmlSystem.entity.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "customers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @NotBlank
    @Size(max = 255)
    @Column(unique = true, nullable = false, name = "client_number")
    private String clientNumber;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "first_name")
    private String firstName;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Size(max = 255)
    @Column(name = "middle_name")
    private String middleName;

    @NotBlank
    @Size(min = 12, max = 12)
    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be 12 digits")
    @Column(nullable = false, unique = true, name = "aadhar_number")
    private String aadharNumber;

    @NotBlank
    @Size(min = 10, max = 10)
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN format")
    @Column(nullable = false, unique = true, name = "pan")
    private String pan;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, name = "occupation")
    private String occupation;

    @NotNull
    @Column(nullable = false, name = "occupation_type",columnDefinition = "occupation_type")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private OccupationType occupationType;

    @NotNull
    @Column(nullable = false, name = "is_pep")
    private Boolean isPep;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "risk_rate",columnDefinition = "severity")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Severity riskRate;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2, name = "monthly_income")
    private BigDecimal monthlyIncome;

    @NotNull
    @Past
    @Column(nullable = false, name = "dob")
    private LocalDate dob;

    @PositiveOrZero
    @Column(precision = 3, scale = 2, name = "profession_multiplier")
    private BigDecimal professionMultiplier;

    @Size(max = 255)
    @Column(name = "family_code")
    private String familyCode;
}
