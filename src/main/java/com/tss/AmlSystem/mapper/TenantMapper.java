package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.response.TenantInlineDto;
import com.tss.AmlSystem.entity.enums.tenant.AccountStatus;
import com.tss.AmlSystem.entity.enums.tenant.AccountType;
import com.tss.AmlSystem.entity.master.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Locale;


@Mapper(componentModel = "spring",
        imports = {Locale.class})
public interface TenantMapper {
    @Mapping(target = "bankName", expression = "java(bankRegisterDto.bankName().toUpperCase(Locale.ROOT))")
    Tenant toTenant(BankRegisterDto bankRegisterDto);
    TenantInlineDto toTenantInlineDto(Tenant tenant);
}
