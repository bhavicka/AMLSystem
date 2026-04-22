package com.tss.AmlSystem.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String schemaName = TenantContext.getCurrentTenant();
        return (schemaName != null) ? schemaName : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() { return true; }
}