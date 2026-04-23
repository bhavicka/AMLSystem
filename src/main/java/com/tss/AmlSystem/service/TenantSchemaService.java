package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.entity.enums.tenant.TenantUserRole;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.TenantUserMapper;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Service
public class TenantSchemaService {
    private final DataSource dataSource;
    private final TenantUserMapper tenantUserMapper;
    private final UserCredentialRepository userCredentialRepository;
    private final TenantUserRepository tenantUserRepository;

    public void createSchema(String name) {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(name)
                .createSchemas(true) // Crucial: tells Flyway to create the schema folder
                .locations("classpath:db/migration/tenant")
                .load()
                .migrate();
        System.out.println("🚀 Startup Complete! Using schema: " + name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void populateTenantSchema(BankRegisterDto bankRegisterDto, UserCredential userCredential, String schemaName){
        TenantUser user = tenantUserMapper.toTenantUser(bankRegisterDto);
        user.setSystemUser(userCredential);
        user.setRole(TenantUserRole.BANK_ADMIN);
        String currentUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCredential currentUser = userCredentialRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found in database"));
        System.out.println(TenantContext.getCurrentTenant());
        user.setCreatedBy(currentUser);
        tenantUserRepository.save(user);
    }
}
