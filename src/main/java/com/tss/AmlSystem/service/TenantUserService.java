package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.response.TenantUserDashboardDto;
import com.tss.AmlSystem.dto.response.TenantUserInlineDto;
import com.tss.AmlSystem.dto.response.TenantUserProfileDto;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.TenantUserMapper;
import com.tss.AmlSystem.repository.TenantRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantUserService {

    private final TenantUserRepository tenantUserRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final TenantRepository tenantRepository;
    private final TenantUserMapper tenantUserMapper;

    public TenantUserProfileDto getUserProfile(String employeeCode){
        TenantUser tenantUser = tenantUserRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("User not found with employee code: " + employeeCode));
        return tenantUserMapper.toTenantUserProfileDto(tenantUser);
    }

    public TenantUserDashboardDto getAllUsers(){
        List<TenantUser> tenantUserList = tenantUserRepository.findAll();
        return new TenantUserDashboardDto(
                tenantUserList.stream()
                        .map(tenantUserMapper::toTenantUserInlineDto)
                        .toList()
        );
    }
}
