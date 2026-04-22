package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Service
public class TenantSchemaService {
    private final DataSource dataSource;
    public void createSchema(String name) {
        String newSchema = name + "_schema";
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(newSchema)
                .createSchemas(true) // Crucial: tells Flyway to create the schema folder
                .locations("classpath:db/migration/tenant")
                .load()
                .migrate();

        TenantContext.setCurrentTenant(newSchema);
        System.out.println("🚀 Startup Complete! Using schema: " + newSchema);
    }
}
