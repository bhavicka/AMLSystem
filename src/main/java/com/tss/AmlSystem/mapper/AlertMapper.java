package com.tss.AmlSystem.mapper;

import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.dto.response.GeneratedAlertDto;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.AlertDetailProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring",uses = AlertTransactionMapper.class)
public interface AlertMapper {

    @Mapping(source = "tenantRule.ruleName", target = "brokenRuleName")
    @Mapping(source = "tenantRule.severityRate", target = "severity")
    @Mapping(source = "createdAt", target = "generatedAt")
    GeneratedAlertDto toGeneratedAlertDto(Alert alert);


    List<GeneratedAlertDto> toGeneratedAlertDtos(List<Alert> alerts);

    @Mapping(source = "projection.alertNumber", target = "alertNumber")
    @Mapping(source = "projection.brokenRuleName", target = "brokenRuleName")
    @Mapping(source = "projection.severity", target = "severity")
    @Mapping(source = "projection.status", target = "status")

    @Mapping(source = "projection.caseReferenceNumber", target = "caseReferenceNumber")
    @Mapping(source = "projection.caseStatus", target = "caseStatus")
    @Mapping(source = "projection.assignedTo", target = "assignedTo")

    @Mapping(source = "projection.transactionCount", target = "transactionCount")
    @Mapping(source = "totalAmount", target = "totalAmount")

    @Mapping(source = "transactions", target = "transactions")
    AlertDetailDto toAlertDetailDto(AlertDetailProjection projection, BigDecimal totalAmount,
                                    List<Transaction> transactions);
}
