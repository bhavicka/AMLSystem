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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        LocalDateTime windowEnd=LocalDateTime.now();
        LocalDateTime windowStart=windowEnd.minusDays(timeWindowInDays);
        LocalDateTime lookBackStart=windowEnd.minusDays(lookBackDays);

        List<String> suspiciousClients=ruleQueryRepository.findIncomeMismatchClients(
                lookBackStart,
                windowEnd,
                windowStart,
                minIncome,
                minTxnCount,
                minTotalTxnAmount,
                multiplierThreshold
        );

        if(suspiciousClients.isEmpty())return;

        List<Alert> generatedAlerts=new ArrayList<>();

        for(String client:suspiciousClients){

            List<Object[]> results=ruleQueryRepository.findFlaggedTransactionsForIncomeMismatch(
                    client,
                    windowStart,
                    windowEnd
            );

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
