package com.tss.AmlSystem.models;

import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;

import java.math.BigDecimal;

public interface AlertDetailProjection {
    String getAlertNumber();
    String getBrokenRuleName();
    Severity getSeverity();
    AlertStatus getStatus();
    String getClientNumber();

    String getCaseReferenceNumber();
    CaseStatus getCaseStatus();
    String getAssignedTo();

    Long getTransactionCount();
    BigDecimal getTotalAmount();
}
