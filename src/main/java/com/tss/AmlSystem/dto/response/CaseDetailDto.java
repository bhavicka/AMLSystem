package com.tss.AmlSystem.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CaseDetailDto {
    private String caseReferenceNumber;
    private String caseStatus;
    private String assignedTo;
    private String assignedBy;
    private List<GeneratedAlertDto> alerts;
}
