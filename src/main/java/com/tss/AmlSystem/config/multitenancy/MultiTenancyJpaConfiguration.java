package com.tss.AmlSystem.config.multitenancy;
import org.hibernate.cfg.AvailableSettings;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenancyJpaConfiguration {

    @Bean
    public HibernatePropertiesCustomizer multiTenancyCustomizer(
            TenantConnectionProvider tenantConnectionProvider,
            TenantIdentifierResolver tenantIdentifierResolver) {

        return properties -> {
            properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, tenantConnectionProvider);
            properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);

            // Explicitly set the strategy
            properties.put("hibernate.multi_tenancy_strategy", "SCHEMA");
            properties.put("hibernate.multi_tenant_connection_provider", tenantConnectionProvider);
            properties.put("hibernate.multi_tenant_identifier_resolver", tenantIdentifierResolver);
        };
    }
}