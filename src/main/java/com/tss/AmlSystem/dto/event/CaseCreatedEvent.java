package com.tss.AmlSystem.dto.event;

public record CaseCreatedEvent(
        String officerEmail,
        String officerName,
        String caseReferenceNumber
) {}
