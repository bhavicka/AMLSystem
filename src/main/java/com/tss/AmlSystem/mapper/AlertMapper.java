package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.dto.response.GeneratedAlertDto;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",uses = AlertTransactionMapper.class)
public interface AlertMapper {

    @Mapping(source = "tenantRule.ruleName", target = "brokenRuleName")
    @Mapping(source = "tenantRule.severityRate", target = "severity")
    @Mapping(source = "createdAt", target = "generatedAt")
    @Mapping(source = "alert.clientNumber", target = "clientNumber")
    GeneratedAlertDto toGeneratedAlertDto(Alert alert);


    List<GeneratedAlertDto> toGeneratedAlertDtos(List<Alert> alerts);

    @Mapping(source = "alert.alertNumber", target = "alertNumber")
    @Mapping(source = "alert.tenantRule.ruleName", target = "brokenRuleName")
    @Mapping(source = "alert.tenantRule.severityRate", target = "severity")
    @Mapping(source = "alert.status", target = "status")
    @Mapping(target = "transactionCount", expression = "java(transactions != null ? transactions.size() : 0)")

    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "transactions", target = "transactions")
    AlertDetailDto toAlertDetailDto(Alert alert, BigDecimal totalAmount,
                                    List<Transaction> transactions);
}
