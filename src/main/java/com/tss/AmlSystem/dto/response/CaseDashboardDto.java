package com.tss.AmlSystem.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CaseDashboardDto {
    private String caseReferenceNumber;
    private String caseStatus;
    private String assignedTo;
}
