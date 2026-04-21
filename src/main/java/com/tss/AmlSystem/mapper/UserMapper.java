package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(BankRegisterDto bankRegisterDto);
}
