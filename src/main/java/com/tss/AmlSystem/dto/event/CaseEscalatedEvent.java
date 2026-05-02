package com.tss.AmlSystem.dto.event;

public record CaseEscalatedEvent(
        String bankAdminEmail,
        String bankAdminName,
        String caseReferenceNumber
) {}
