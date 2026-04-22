package com.tss.AmlSystem.config.multitenancy;

public class TenantContext {

    // ThreadLocal holds the tenant string for the current thread
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(String schemaName) {
        CURRENT_TENANT.set(schemaName);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
