package com.tss.AmlSystem.models;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class RuleContext {
    private List<Transaction> transactionList;
    private TenantRule tenantRule;
    private Map<String,String> params;
    private LocalDate lookBackDays;
}
