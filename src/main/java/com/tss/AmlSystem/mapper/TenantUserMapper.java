package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.ComplianceOfficerRegisterDto;
import com.tss.AmlSystem.dto.response.TenantUserInlineDto;
import com.tss.AmlSystem.dto.response.TenantUserProfileDto;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantUserMapper {
    TenantUser toTenantUser(BankRegisterDto bankRegisterDto);
    TenantUser toTenantUser(ComplianceOfficerRegisterDto complianceOfficerRegisterDto);

    TenantUserProfileDto toTenantUserProfileDto(TenantUser tenantUser);

    @Mapping(target = "activeWorkload",ignore = true)
    TenantUserInlineDto toTenantUserInlineDto(TenantUser tenantUser);
}
