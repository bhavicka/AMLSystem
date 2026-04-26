package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.RuleQueryRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("STRUCTURING")
@RequiredArgsConstructor
public class StructuringRuleEvaluator implements RuleEvaluator {

    private final AlertRepository alertRepository;
    private final RuleQueryRepository ruleQueryRepository;

    @Override
    public void evaluate(RuleContext ruleContext) {

        Map<String, String> params = ruleContext.getParams();

        BigDecimal perTxnThreshold  = new BigDecimal(params.get("per_txn_threshold_amount"));
        BigDecimal totalThreshold   = new BigDecimal(params.get("total_threshold_amount"));
        int timeWindowInDays    = Integer.parseInt(params.get("time_window"));
        int minimumTxns         = Integer.parseInt(params.get("minimum_transactions"));
        int lookBackDays        = Integer.parseInt(params.get("look_back_days"));

        if (timeWindowInDays > lookBackDays) {
            throw new IllegalArgumentException("Time window cannot be greater than look back days");
        }

        LocalDateTime windowEnd   = LocalDateTime.now();
        LocalDateTime windowStart = windowEnd.minusDays(timeWindowInDays);
        LocalDateTime lookBackStart = windowEnd.minusDays(lookBackDays);

        List<String> suspiciousClients = ruleQueryRepository.findSuspiciousClientsForStructuring(
                lookBackStart,
                windowEnd,
                windowStart,
                perTxnThreshold,
                minimumTxns,
                totalThreshold
        );

        if (suspiciousClients.isEmpty()) return;

        List<Alert> generatedAlerts = new ArrayList<>();

        for (String client : suspiciousClients) {

            List<Object[]> results = ruleQueryRepository.findFlaggedTransactionsForStructuring(
                    client,
                    perTxnThreshold,
                    windowStart,
                    windowEnd
            );

            List<Transaction> flaggedTxns = new ArrayList<>();
            for (Object[] row : results) {
                Transaction t = new Transaction();
                t.setId((Long) row[0]);
                t.setAccountNumber((String) row[1]);
                t.setAmount((BigDecimal) row[2]);
                t.setTransactionDate((LocalDate) row[3]);
                flaggedTxns.add(t);
            }

            if (flaggedTxns.isEmpty()) continue;

            boolean exists = alertRepository.existsByClientNumberAndTenantRuleAndWindowStart(
                    client,
                    ruleContext.getTenantRule(),
                    windowStart
            );

            if (exists) continue;

            Alert alert = new Alert();
            alert.setClientNumber(client);
            alert.setTenantRule(ruleContext.getTenantRule());
            alert.setStatus(AlertStatus.NEW);
            alert.setTransactions(flaggedTxns);
            alert.setCreatedAt(LocalDateTime.now());
            alert.setAlertNumber(UniqueNumberGenerator.generateAlertNumber());

            generatedAlerts.add(alert);
        }

        alertRepository.saveAll(generatedAlerts);
    }
}