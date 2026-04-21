package com.tss.AmlSystem.service;

import com.tss.AmlSystem.entity.tenant.RuleParameter;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.RuleParameterRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import com.tss.AmlSystem.strategy.RuleEvaluator;
import com.tss.AmlSystem.strategy.RuleFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleExecutionService {
    private final RuleFactory ruleFactory;
    private final TransactionRepository transactionRepository;
    private final RuleParameterRepository ruleParameterRepository;

    public void runRule(TenantRule rule, LocalDate lookBackDays){

        //Take out parameters from rule
        Map<String, String> params =
                ruleParameterRepository.findByRuleId(rule.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                RuleParameter::getParamKey,
                                RuleParameter::getParamValue
                        ));

        //take out transactions with lookback
        List<Transaction> transactionList=transactionRepository.findRecentTransactions(lookBackDays);

        //get evaluator object from factory
        RuleEvaluator evaluator= ruleFactory.getRuleEvaluator(rule.getRuleCode());

        //build context to give it to evaluate
        RuleContext context=new RuleContext(transactionList,rule,params,lookBackDays);

        evaluator.evaluate(context);
    }
}
