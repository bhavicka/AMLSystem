package com.tss.AmlSystem.dto.response;

import java.util.List;

public record TenantUserDashboardDto(
        List<TenantUserInlineDto> userInlineDtoList
) {
}
