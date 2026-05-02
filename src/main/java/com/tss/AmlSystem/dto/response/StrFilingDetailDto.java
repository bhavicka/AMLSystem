package com.tss.AmlSystem.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StrFilingDetailDto {
    public String caseReferenceNumber;
    public String clientNumber;
    public String filedBy;
    public String referenceNumber;
    public String filedAt;
    public String pdfStoragePath;
}
