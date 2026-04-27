package com.tss.AmlSystem.strategy.rule;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Component("STRUCTURING")
@RequiredArgsConstructor
@Slf4j
public class StructuringRuleEvaluator implements RuleEvaluator {

    private final AlertRepository alertRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void evaluate(RuleContext ruleContext) {
        Map<String, String> params = ruleContext.getParams();

        double perTxnThreshold   = Double.parseDouble(params.get("per_txn_threshold_amount"));
        double totalThreshold    = Double.parseDouble(params.get("total_threshold_amount"));
        int    timeWindowInDays  = Integer.parseInt(params.get("time_window"));
        int    minimumTxns       = Integer.parseInt(params.get("minimum_transactions"));

        LocalDateTime windowEnd   = LocalDateTime.now();
        LocalDateTime ruleWindowStart = windowEnd.minusDays(timeWindowInDays);
        LocalDateTime lookBackStart=ruleContext.getLookBackDays().atStartOfDay();

        LocalDateTime effectiveStart=lookBackStart.isAfter(ruleWindowStart)
                ?lookBackStart:ruleWindowStart;

        log.debug("{} Executing STRUCTURING rule evaluation. Threshold: {}, Min Txns: {}, Window: {} days", LogTag.RULE.getValue(), totalThreshold, minimumTxns, timeWindowInDays);

        // Step 1: Find all account numbers that match the structuring pattern.
        // Each transaction is below perTxnThreshold (avoiding detection),
        // but together they exceed totalThreshold within the time window.
        String suspiciousAccountsQuery = """
                SELECT t.account_number
                FROM transactions t
                WHERE t.amount < ?
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN ? AND ?
                GROUP BY t.account_number
                HAVING COUNT(t.id) >= ?
                   AND SUM(t.amount) > ?
                """;

        List<String> suspiciousAccounts = jdbcTemplate.queryForList(
                suspiciousAccountsQuery,
                String.class,
                perTxnThreshold,
                effectiveStart,
                windowEnd,
                minimumTxns,
                totalThreshold
        );

        log.info("{} Found {} suspicious accounts for structuring", LogTag.RULE.getValue(), suspiciousAccounts.size());
        if (suspiciousAccounts.isEmpty()) return;

        // Step 2: For each suspicious account, fetch its transactions
        // and raise one alert grouping them all.
        String txnFetchQuery = """
                SELECT t.id, t.account_number, t.amount, t.transaction_date
                FROM transactions t
                WHERE t.account_number = ?
                  AND t.amount < ?
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN ? AND ?
                ORDER BY t.transaction_date DESC
                """;

        for (String accountNumber : suspiciousAccounts) {

            List<Transaction> flaggedTxns = jdbcTemplate.query(
                    txnFetchQuery,
                    (rs, rowNum) -> {
                        Transaction t = new Transaction();
                        t.setId(rs.getLong("id"));
                        t.setAccountNumber(rs.getString("account_number"));
                        t.setAmount(rs.getBigDecimal("amount"));
                        t.setTransactionDate(rs.getObject("transaction_date", LocalDate.class));
                        return t;
                    },
                    accountNumber, perTxnThreshold, effectiveStart, windowEnd
            );

            // Step 3: Avoid duplicate alerts — skip if an open alert
            // already exists for this account + rule within the window
            boolean alreadyAlerted = alertRepository
                    .existsByAccountNumberAndTenantRuleAndGeneratedAtAfter(
                            accountNumber,
                            ruleContext.getTenantRule(),
                            effectiveStart,
                            AlertStatus.NEW
                    );

            if (alreadyAlerted) continue;

            // Step 4: Persist the alert
            Alert alert = new Alert();
            alert.setTransactions(flaggedTxns);
            alert.setTenantRule(ruleContext.getTenantRule());
            alert.setStatus(AlertStatus.NEW);
            alert.setCreatedAt(LocalDateTime.now());

            alertRepository.save(alert);
            log.info("{} {} Generated Structuring Alert for Account: {}", LogTag.RULE.getValue(), LogTag.SECURITY.getValue(), accountNumber);
        }
    }
}
