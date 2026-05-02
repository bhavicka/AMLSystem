package com.tss.AmlSystem.dto.pdf;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class StrAlertDto {
    public String ruleCode, alertNumber;
    public LocalDate date;
}
