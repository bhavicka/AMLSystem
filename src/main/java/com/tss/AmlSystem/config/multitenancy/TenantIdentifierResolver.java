package com.tss.AmlSystem.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        // Fallback to "public" or "master" if no tenant is set
        return (tenantId != null) ? tenantId : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() { return true; }
}