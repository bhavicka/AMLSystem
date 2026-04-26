package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
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
    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void evaluate(RuleContext ruleContext) {

        Map<String, String> params = ruleContext.getParams();

        double perTxnThreshold  = Double.parseDouble(params.get("per_txn_threshold_amount"));
        double totalThreshold   = Double.parseDouble(params.get("total_threshold_amount"));
        int timeWindowInDays    = Integer.parseInt(params.get("time_window"));
        int minimumTxns         = Integer.parseInt(params.get("minimum_transactions"));
        int lookBackDays        = Integer.parseInt(params.get("look_back_days"));

        if (timeWindowInDays > lookBackDays) {
            throw new IllegalArgumentException("Time window cannot be greater than look back days");
        }

        LocalDateTime windowEnd   = LocalDateTime.now();
        LocalDateTime windowStart = windowEnd.minusDays(timeWindowInDays);
        LocalDateTime lookBackStart = windowEnd.minusDays(lookBackDays);

//        System.out.println(jdbcTemplate.queryForObject("SHOW search_path", String.class));

        String suspiciousClientsQuery = """
                SELECT td.client_number
                FROM (
                    SELECT a.client_number,
                           t.amount,
                           t.transaction_date
                    FROM transactions t
                    JOIN accounts a ON t.account_number = a.account_number
                    WHERE t.transaction_type = 'CREDIT'
                      AND t.transaction_date BETWEEN ? AND ?
                ) AS td
                WHERE td.transaction_date BETWEEN ? AND ?
                  AND td.amount < ?
                GROUP BY td.client_number
                HAVING COUNT(*) >= ?
                   AND SUM(td.amount) > ?
                """;

        List<String> suspiciousClients = entityManager.createNativeQuery(suspiciousClientsQuery)
                .setParameter(1, lookBackStart)
                .setParameter(2, windowEnd)
                .setParameter(3, windowStart)
                .setParameter(4, windowEnd)
                .setParameter(5, perTxnThreshold)
                .setParameter(6, minimumTxns)
                .setParameter(7, totalThreshold)
                .getResultList();

        if (suspiciousClients.isEmpty()) return;

        List<Alert> generatedAlerts = new ArrayList<>();

        String txnFetchQuery = """
                SELECT t.id, t.account_number, t.amount, t.transaction_date
                FROM transactions t
                JOIN accounts a ON t.account_number = a.account_number
                WHERE a.client_number = ?
                  AND t.amount < ?
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN ? AND ?
                ORDER BY t.transaction_date DESC
                """;

        for (String client : suspiciousClients) {

            List<Object[]> results = entityManager.createNativeQuery(txnFetchQuery)
                    .setParameter(1, client)
                    .setParameter(2, perTxnThreshold)
                    .setParameter(3, windowStart)
                    .setParameter(4, windowEnd)
                    .getResultList();

            List<Transaction> flaggedTxns = new ArrayList<>();
            for (Object[] row : results) {
                Transaction t = new Transaction();
                t.setId(((Number) row[0]).longValue());
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