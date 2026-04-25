package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.ComplianceOfficerRegisterDto;
import com.tss.AmlSystem.entity.master.UserCredential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserCredentialMapper {
    @Mapping(target = "email", source = "bankAdminEmail")
    @Mapping(target = "passwordHash", ignore = true)
    UserCredential toUserCredential(BankRegisterDto bankRegisterDto);
    UserCredential toUserCredential(ComplianceOfficerRegisterDto complianceOfficerRegisterDto);


}
