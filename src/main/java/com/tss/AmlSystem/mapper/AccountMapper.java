package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.AccountBatchProcessDto;
import com.tss.AmlSystem.entity.tenant.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Locale;
import com.tss.AmlSystem.entity.enums.tenant.AccountStatus;
import com.tss.AmlSystem.entity.enums.tenant.AccountType;
import org.springframework.context.annotation.Import;

@Mapper(
        componentModel = "spring",
        imports = {AccountType.class,
            AccountStatus.class,
            Locale.class}
)

public interface AccountMapper {

    @Mapping(target = "clientNumber", expression = "java(processDto.getClientNumber().trim())")
    @Mapping(target = "accountNumber", expression = "java(processDto.getAccountNumber().trim())")
    @Mapping(target = "accountType", expression = "java(AccountType.valueOf(processDto.getAccountType().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "accountStatus", expression = "java(processDto.getAccountStatus() != null ? AccountStatus.valueOf(processDto.getAccountStatus().trim().toUpperCase(Locale.ROOT)) : null)")
    Account toAccount(AccountBatchProcessDto processDto);
}
