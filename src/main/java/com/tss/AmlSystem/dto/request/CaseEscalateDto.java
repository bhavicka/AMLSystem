package com.tss.AmlSystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CaseEscalateDto {
    String caseReferenceNumber;
    String action;
    String notes;
}
