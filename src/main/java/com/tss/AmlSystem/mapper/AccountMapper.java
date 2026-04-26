package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "clientNumber", expression = "java(processDto.clientNumber().trim())")
    @Mapping(target = "accountNumber", expression = "java(processDto.accountNumber().trim())")
    @Mapping(target = "accountType", expression = "java(AccountType.valueOf(processDto.accountType().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "accountStatus", expression = "java(AccountStatus.valueOf(processDto.accountStatus().trim().toUpperCase(Locale.ROOT)))")
    Account toAccount(AccountBatchProcessDto processDto);
}
