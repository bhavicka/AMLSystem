package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.RuleQueryRepository;
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

@Component("INCOME_MISMATCH")
@RequiredArgsConstructor
public class IncomeMismatchRuleEvaluator implements RuleEvaluator {
    private final AlertRepository alertRepository;
    private final RuleQueryRepository ruleQueryRepository;

    @Override
    public void evaluate(RuleContext ruleContext) {
        Map<String,String> params=ruleContext.getParams();

        int timeWindowInDays=Integer.parseInt(params.get("time_window"));
        double multiplierThreshold=Double.parseDouble(params.get("income_multiplier_threshold"));
        BigDecimal minIncome=new BigDecimal(params.get("min_income"));
        BigDecimal minTotalTxnAmount=new BigDecimal(params.get("min_total_transaction_amount"));
        int minTxnCount=Integer.parseInt(params.get("min_transaction_count"));
        int lookBackDays=Integer.parseInt(params.get("look_back_days"));

        if(timeWindowInDays>lookBackDays){
            throw new IllegalArgumentException("Time window cannot be greater than look back days");
        }

        LocalDateTime lookBackStart=LocalDateTime.now().minusDays(lookBackDays);

        List<Object[]> suspiciousClientsWithRange=ruleQueryRepository.findIncomeMismatchSlidingWindow(
                lookBackStart,
                LocalDateTime.now(),
                minIncome,
                minTxnCount,
                minTotalTxnAmount,
                multiplierThreshold,
                timeWindowInDays
        );

        if(suspiciousClientsWithRange.isEmpty())return;
        Map<String, List<Transaction>> clientTxnMap = new HashMap<>();



        for(Object obj:suspiciousClientsWithRange) {

            String client = (String) ((Object[]) obj)[0];
            LocalDate windowEnd = (LocalDate) ((Object[]) obj)[1];
            LocalDate windowStart = (LocalDate) ((Object[]) obj)[2];

            List<Object[]> results = ruleQueryRepository.findFlaggedTransactionsForIncomeMismatch(
                    client,
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
        List<Alert> generatedAlerts=new ArrayList<>();

        for(Map.Entry<String,List<Transaction>> entry: clientTxnMap.entrySet()) {
            String client = entry.getKey();
            List<Transaction> transactions = entry.getValue();


            boolean exists = alertRepository.existsByClientNumberAndTenantRuleIdAndCreatedAfter(
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
