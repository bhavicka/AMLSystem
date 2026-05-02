package com.tss.AmlSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TenantDashboardDto {
    public List<TenantInlineDto> tenantInlineDtoList;
}
