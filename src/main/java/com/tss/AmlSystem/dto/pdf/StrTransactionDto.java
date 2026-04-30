package com.tss.AmlSystem.dto.pdf;

import java.time.LocalDate;

public class StrTransactionDto {
    public LocalDate date;
    public String refNumber, accNumber, receiverAccNumber, amount, type, mode;
}
