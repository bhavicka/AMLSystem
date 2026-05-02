package com.tss.AmlSystem.dto.pdf;

import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class StrTransactionDto {
    public LocalDate date;
    public String refNumber, accNumber, receiverAccNumber, type, mode;
    public Double amount;
}
