package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.response.TenantDashboardDto;
import com.tss.AmlSystem.dto.response.TenantDetailsDto;
import com.tss.AmlSystem.entity.enums.tenant.TenantUserRole;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.TenantMapper;
import com.tss.AmlSystem.repository.TenantRepository;
import com.tss.AmlSystem.repository.TenantRuleRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {
    private final TenantRepository tenantRepository;
    private final TenantUserRepository tenantUserRepository;
    private final TenantRuleRepository tenantRuleRepository;
    private final TenantMapper tenantMapper;

    public TenantDashboardDto getAllTenants(){
        List<Tenant>  tenantList = tenantRepository.findAll();
        return new TenantDashboardDto(
                tenantList.stream().map(tenantMapper::toTenantInlineDto).toList()
        );
    }

    public TenantDetailsDto getTenantByBankName(String bankName){
        bankName = bankName.replace("-", " ").toUpperCase(Locale.ROOT);
        String finalBankName = bankName;
        Tenant tenant = tenantRepository.findByBankName(bankName)
                .orElseThrow(() -> new RuntimeException("Tenant not found with bank name: " + finalBankName));

        TenantDetailsDto tenantDetailsDto = new TenantDetailsDto();
        tenantDetailsDto.setBankName(tenant.getBankName());
        tenantDetailsDto.setIfsc(tenant.getIfsc());
        tenantDetailsDto.setContactEmail(tenant.getContactEmail());
        tenantDetailsDto.setSchemaName(tenant.getSchemaName());
        tenantDetailsDto.setCreatedAt(tenant.getCreatedAt().toLocalDate());

        TenantContext.clear();
        TenantContext.setCurrentTenant(tenant.getSchemaName());

        tenantDetailsDto.setTenantUsersCount(tenantUserRepository.count());
        TenantUser tenantUser = tenantUserRepository.findByRole(TenantUserRole.BANK_ADMIN).get(0);

        tenantDetailsDto.setBankAdminName(tenantUser.getFirstName()+" "+tenantUser.getLastName());
        tenantDetailsDto.setBankAdminEmail(tenantUser.getEmail());

        List<TenantRule> tenantRules = tenantRuleRepository.findByIsActiveTrue();
        tenantDetailsDto.setRuleCodes(tenantRules.stream().map(TenantRule::getRuleCode).toList());
        TenantContext.clear();
        TenantContext.setCurrentTenant("public");
        return tenantDetailsDto;
    }

}
