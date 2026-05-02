package com.tss.AmlSystem.config.multitenancy;

import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {

    // ThreadLocal holds the tenant string for the current thread
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(String schemaName) {
        log.info("{} Setting current tenant to: {}", schemaName, LogTag.TENANT.getValue());
        CURRENT_TENANT.set(schemaName);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        log.info("{} Clearing current tenant: {}", LogTag.TENANT.getValue(), getCurrentTenant());
        CURRENT_TENANT.remove();
    }
}
