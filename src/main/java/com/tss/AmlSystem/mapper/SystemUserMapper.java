package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.entity.SystemUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemUserMapper {
    SystemUser toSystemUser(BankRegisterDto bankRegisterDto);
}
