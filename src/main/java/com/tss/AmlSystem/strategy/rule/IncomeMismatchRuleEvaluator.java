package com.tss.AmlSystem.strategy.rule;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.RuleQueryRepository;
import com.tss.AmlSystem.service.AlertService;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component("INCOME_MISMATCH")
@RequiredArgsConstructor
public class IncomeMismatchRuleEvaluator implements RuleEvaluator {
    private final AlertRepository alertRepository;
    private final AlertService alertService;
    private final RuleQueryRepository ruleQueryRepository;
    private final EntityManager entityManager;

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
        Map<String, Set<Long>> clientTxnMap = new HashMap<>();

        for(Object obj:suspiciousClientsWithRange) {

            String client = (String) ((Object[]) obj)[0];
            LocalDate windowEnd = (LocalDate) ((Object[]) obj)[1];
            LocalDate windowStart = (LocalDate) ((Object[]) obj)[2];

            List<Long> results = ruleQueryRepository.findFlaggedTransactionsForIncomeMismatch(
                    client,
                    windowStart,
                    windowEnd
            );

            for (Long id : results) {
                clientTxnMap
                        .computeIfAbsent(client, k -> new HashSet<>())
                        .add(id);
            }

        }

        if(clientTxnMap.isEmpty())return;

        for(Map.Entry<String,Set<Long>> entry: clientTxnMap.entrySet()) {
            String client = entry.getKey();

            List<Long> sortedTransactionIds=entry.getValue().stream().sorted().toList();

            String base=client+"|"+ruleContext.getTenantRule().getId()+"|"+
                    sortedTransactionIds.stream().map(String::valueOf).collect(Collectors.joining(","));

            String hash= DigestUtils.sha256Hex(base);

            List<Transaction> transactions = entry.getValue()
                    .stream()
                    .map(id -> entityManager.getReference(Transaction.class, id))
                    .toList();


            alertService.saveAlert(client,ruleContext,transactions,hash);

        }


    }
}
