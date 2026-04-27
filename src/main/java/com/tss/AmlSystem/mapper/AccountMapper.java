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

    @Mapping(target = "clientNumber", expression = "java(processDto.clientNumber().trim())")
    @Mapping(target = "accountNumber", expression = "java(processDto.accountNumber().trim())")
    @Mapping(target = "accountType", expression = "java(AccountType.valueOf(processDto.accountType().trim().toUpperCase(Locale.ROOT)))")
    @Mapping(target = "accountStatus", expression = "java(processDto.accountStatus() != null ? AccountStatus.valueOf(processDto.accountStatus().trim().toUpperCase(Locale.ROOT)) : null)")
    Account toAccount(AccountBatchProcessDto processDto);
}
