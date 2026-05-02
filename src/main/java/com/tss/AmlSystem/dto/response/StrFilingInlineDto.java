package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StrFilingInlineDto {
    private String caseNumber;
    private String referenceNumber;
    private String filedBy;
}
