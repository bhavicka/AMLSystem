package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AlertDetailDto {
    private String alertNumber;
    private String brokenRuleName;
    private Severity severity;
    private AlertStatus status;
    private String clientNumber;

    private String caseReferenceNumber;
    private CaseStatus caseStatus;
    private String assignedTo;

    private Integer transactionCount;
    private BigDecimal totalAmount;

    private List<AlertTransactionDto> transactions;
}
