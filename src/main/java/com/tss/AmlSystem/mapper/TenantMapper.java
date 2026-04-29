package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.response.TenantInlineDto;
import com.tss.AmlSystem.entity.master.Tenant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    Tenant toTenant(BankRegisterDto bankRegisterDto);
    TenantInlineDto toTenantInlineDto(Tenant tenant);
}
