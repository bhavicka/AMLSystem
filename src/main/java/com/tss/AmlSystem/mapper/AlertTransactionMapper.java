package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.AlertTransactionDto;
import com.tss.AmlSystem.entity.tenant.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertTransactionMapper {

    AlertTransactionDto toDto(Transaction transaction);
}
