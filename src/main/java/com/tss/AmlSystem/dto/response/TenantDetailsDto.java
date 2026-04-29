package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantDetailsDto {
    public String bankName, ifsc, contactEmail, schemaName;
    public LocalDate createdAt;
    Long tenantUsersCount;
    String bankAdminName, bankAdminEmail;
    List<String> ruleCodes;
}
