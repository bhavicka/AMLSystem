package com.tss.AmlSystem.strategy.rule;

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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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

        LocalDateTime lookBackStart = LocalDateTime.now().minusDays(lookBackDays);

        List<Object[]> suspiciousClientsWithRange = ruleQueryRepository.findStructuringSlidingWindow(
                lookBackStart,
                LocalDateTime.now(),
                perTxnThreshold,
                minimumTxns,
                totalThreshold,
                timeWindowInDays
        );

        if (suspiciousClientsWithRange.isEmpty()) return;
        Map<String,List<Transaction>> clientTxnMap=new HashMap<>();


        for (Object[] obj : suspiciousClientsWithRange) {

            String client = (String) obj[0];
            LocalDate windowEnd = ((LocalDate) obj[1]);
            LocalDate windowStart = ((LocalDate) obj[2]);

            List<Object[]> results =
                    ruleQueryRepository.findFlaggedTransactionsForStructuring(
                            client,
                            perTxnThreshold,
                            windowStart,
                            windowEnd
                    );

            for (Object[] row : results) {

                Transaction txn = new Transaction();
                txn.setId(((Number) row[0]).longValue());
                txn.setAccountNumber((String) row[1]);
                txn.setAmount((BigDecimal) row[2]);
                txn.setTransactionDate((LocalDate) row[3]);

                clientTxnMap
                        .computeIfAbsent(client, k -> new ArrayList<>())
                        .add(txn);
            }
        }

        if(clientTxnMap.isEmpty())return;

        List<Alert> generatedAlerts = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : clientTxnMap.entrySet()) {

            String client = entry.getKey();
            List<Transaction> transactions = entry.getValue();

            boolean exists = alertRepository
                    .existsByClientNumberAndTenantRuleIdAndCreatedAfter(
                            client,
                            ruleContext.getTenantRule().getId(),
                            lookBackStart
                    );

            if (exists) continue;

            Alert alert = new Alert();
            alert.setClientNumber(client);
            alert.setTenantRule(ruleContext.getTenantRule());
            alert.setStatus(AlertStatus.NEW);
            alert.setTransactions(transactions);
            alert.setCreatedAt(LocalDateTime.now());
            alert.setAlertNumber(UniqueNumberGenerator.generateAlertNumber());

            generatedAlerts.add(alert);
        }

        alertRepository.saveAll(generatedAlerts);
    }
}