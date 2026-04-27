package com.tss.AmlSystem.dto.request;

public record AccountBatchProcessDto(
        String clientNumber,
        String accountNumber,
        String accountType,
        String accountStatus
) { }