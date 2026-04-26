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

@Component("INCOME_MISMATCH")
@RequiredArgsConstructor
public class IncomeMismatchRuleEvaluator implements RuleEvaluator {
    private final AlertRepository alertRepository;
    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void evaluate(RuleContext ruleContext) {
        Map<String,String> params=ruleContext.getParams();

        int timeWindowInDays=Integer.parseInt(params.get("time_window"));
        double multiplierThreshold=Double.parseDouble(params.get("income_multiplier_threshold"));
        double minIncome=Double.parseDouble(params.get("min_income"));
        double minTotalTxnAmount=Double.parseDouble(params.get("min_total_transaction_amount"));
        int minTxnCount=Integer.parseInt(params.get("min_transaction_count"));
        int lookBackDays=Integer.parseInt(params.get("look_back_days"));

        if(timeWindowInDays>lookBackDays){
            throw new IllegalArgumentException("Time window cannot be greater than look back days");
        }

        LocalDateTime windowEnd=LocalDateTime.now();
        LocalDateTime windowStart=windowEnd.minusDays(timeWindowInDays);
        LocalDateTime lookBackStart=windowEnd.minusDays(lookBackDays);

        String suspiciousCustomerQuery= """
                SELECT td.client_number
                FROM
                
                (SELECT c.client_number,c.monthly_income,
                        c.profession_multiplier,
                        t.amount,t.transaction_date FROM customers c
                    JOIN accounts a ON c.client_number=a.client_number
                    JOIN transactions t ON t.account_number=a.account_number
                    WHERE t.transaction_type='CREDIT'
                        AND t.transaction_date BETWEEN ? AND ?
                ) AS td
                
                WHERE td.transaction_date BETWEEN ? AND ?
                    AND td.monthly_income IS NOT NULL AND td.monthly_income >= ?
                    GROUP BY td.client_number, td.monthly_income, td.profession_multiplier
                    HAVING COUNT(*) >= ?
                        AND SUM(td.amount) >= ?
                        AND SUM(td.amount) > (td.monthly_income * td.profession_multiplier * ?)
                """;

        List<String> suspiciousClients=entityManager.createNativeQuery(suspiciousCustomerQuery)
                .setParameter(1, lookBackStart)
                .setParameter(2, windowEnd)
                .setParameter(3, windowStart)
                .setParameter(4, windowEnd)
                .setParameter(5, minIncome)
                .setParameter(6, minTxnCount)
                .setParameter(7, minTotalTxnAmount)
                .setParameter(8, multiplierThreshold)
                .getResultList();

        if(suspiciousClients.isEmpty())return;

        List<Alert> generatedAlerts=new ArrayList<>();

        String txnFetchQuery= """
                SELECT t.id,t.account_number,t.amount,t.transaction_date
                FROM transactions t
                JOIN accounts a ON t.account_number=a.account_number
                WHERE a.client_number = ?
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN ? AND ?
                ORDER BY t.transaction_date DESC
                """;

        for(String client:suspiciousClients){

            List<Object[]> results=entityManager.createNativeQuery(txnFetchQuery)
                    .setParameter(1, client)
                    .setParameter(2, windowStart)
                    .setParameter(3, windowEnd)
                    .getResultList();

            List<Transaction> flaggedTxns=new ArrayList<>();

            for(Object[] row:results){
                Transaction txn=new Transaction();
                txn.setId(((Number)row[0]).longValue());
                txn.setAccountNumber((String)row[1]);
                txn.setAmount((BigDecimal) row[2]);
                txn.setTransactionDate(((LocalDate)row[3]));

                flaggedTxns.add(txn);
            }

            if(flaggedTxns.isEmpty())continue;

            boolean exists=alertRepository.existsByClientNumberAndTenantRuleAndWindowStart(
                    client,
                    ruleContext.getTenantRule(),
                    windowStart
            );

            if(exists)continue;

            Alert alert=new Alert();
            alert.setClientNumber(client);
            alert.setTenantRule(ruleContext.getTenantRule());
            alert.setStatus(AlertStatus.NEW);
            alert.setTransactions(flaggedTxns);
            alert.setAlertNumber(UniqueNumberGenerator.generateAlertNumber());

            generatedAlerts.add(alert);
        }

        alertRepository.saveAll(generatedAlerts);


    }
}
