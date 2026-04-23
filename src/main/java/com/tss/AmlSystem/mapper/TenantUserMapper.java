package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.ComplianceOfficerRegisterDto;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantUserMapper {
    TenantUser toTenantUser(BankRegisterDto bankRegisterDto);
    TenantUser toTenantUser(ComplianceOfficerRegisterDto complianceOfficerRegisterDto);

}
