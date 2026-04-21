package com.tss.AmlSystem.entity;

import com.tss.AmlSystem.entity.enums.OccupationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.grammars.hql.HqlParser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "customers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity{
    @Column(unique = true, nullable = false)
    private String clientNumber;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String middleName;
    @Column(nullable = false, unique = true)
    private String aadharNumber;
    @Column(nullable = false, unique = true)
    private String pan;
    @Column(nullable = false)
    private String occupation;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OccupationType occupationType;
    @Column(nullable = false)
    private Boolean isPep;
    @Min(value = 1)
    @Max(value = 10)
    private Integer riskRate;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyIncome;
    @Column(nullable = false)
    private LocalDate dob;
    @Column(precision = 5, scale = 2)
    private BigDecimal professionMultiplier;
    private String familyCode;
}
