package com.tss.AmlSystem.strategy.rule;

import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.exception.BusinessValidationException;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.RuleQueryRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import com.tss.AmlSystem.service.AlertService;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Component("STRUCTURING")
@RequiredArgsConstructor
@Slf4j
public class StructuringRuleEvaluator implements RuleEvaluator {

    private final AlertService alertService;
    private final RuleQueryRepository ruleQueryRepository;
    private final EntityManager entityManager;

    @Override
    public void evaluate(RuleContext ruleContext) {

        Map<String, String> params = ruleContext.getParams();

        BigDecimal perTxnThreshold  = new BigDecimal(params.get("per_txn_threshold_amount"));
        BigDecimal totalThreshold   = new BigDecimal(params.get("total_threshold_amount"));
        int timeWindowInDays    = Integer.parseInt(params.get("time_window"));
        int minimumTxns         = Integer.parseInt(params.get("minimum_transactions"));
        int lookBackDays        = Integer.parseInt(params.get("look_back_days"));

        if (timeWindowInDays > lookBackDays) {
            throw new BusinessValidationException("Time window cannot be greater than look back days");
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

        if (suspiciousClientsWithRange.isEmpty()) {
            log.info("{} No initial structuring patterns found", LogTag.RULE.getValue());
            return;
        }
        Map<String, Set<Long>> clientTxnMap=new HashMap<>();


        for (Object[] obj : suspiciousClientsWithRange) {

            String client = (String) obj[0];
            LocalDate windowEnd = ((LocalDate) obj[1]);
            LocalDate windowStart = ((LocalDate) obj[2]);

            List<Long> results =
                    ruleQueryRepository.findFlaggedTransactionsForStructuring(
                            client,
                            perTxnThreshold,
                            windowStart,
                            windowEnd
                    );

            for (Long id : results) {
                clientTxnMap
                        .computeIfAbsent(client, k -> new HashSet<>())
                        .add(id);
            }
        }

        if(clientTxnMap.isEmpty()) {
            log.info("{} No confirmed structuring chunks found after filtering", LogTag.RULE.getValue());
            return;
        }


        for (Map.Entry<String, Set<Long>> entry : clientTxnMap.entrySet()) {

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
            log.info("{} {} Generated Structuring Alert for Account: {}", LogTag.RULE.getValue(), LogTag.SECURITY.getValue(), client);
        }

    }
}