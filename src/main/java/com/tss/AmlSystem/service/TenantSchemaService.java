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
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TenantSchemaService {
    private final DataSource dataSource;
    private final TenantUserMapper tenantUserMapper;
    private final UserCredentialRepository userCredentialRepository;
    private final TenantUserRepository tenantUserRepository;

    public void createSchema(String name) {
        log.info("{} Initiating Flyway migration to create schema: {}", LogTag.TENANT.getValue(), name);
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(name)
                .createSchemas(true)
                .locations("classpath:db/migration/tenant")
                .load()
                .migrate();
        log.info("{} {} Startup Complete! Successfully migrated schema: {}", LogTag.SYSTEM.getValue(), LogTag.TENANT.getValue(), name);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void populateTenantSchema(BankRegisterDto bankRegisterDto, UserCredential userCredential){
        log.info("{} Populating initial setup for tenant schema", LogTag.TENANT.getValue());
        TenantUser user = tenantUserMapper.toTenantUser(bankRegisterDto);
        user.setSystemUser(userCredential);
        user.setRole(TenantUserRole.BANK_ADMIN);
        user.setEmail(bankRegisterDto.bankAdminEmail());
        String currentUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserCredential currentUser = userCredentialRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> {
                    log.error("{} {} Current user not found while populating schema", LogTag.TENANT.getValue(), LogTag.SECURITY.getValue());
                    return new RuntimeException("Current user not found in database");
                });
        log.debug("{} Verified context is within schema: {}", LogTag.TENANT.getValue(), TenantContext.getCurrentTenant());
        user.setCreatedBy(currentUser);
        tenantUserRepository.save(user);
    }
}
