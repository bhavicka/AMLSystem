package com.tss.AmlSystem.service;

import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.TenantRuleParameter;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.TenantRuleParameterRepository;
import com.tss.AmlSystem.repository.TransactionRepository;
import com.tss.AmlSystem.strategy.rule.RuleEvaluator;
import com.tss.AmlSystem.factory.RuleFactory;
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
    private final TenantRuleParameterRepository tenantRuleParameterRepository;

    public void runRule(TenantRule rule){

        //Take out parameters from rule
        Map<String, String> params =
                tenantRuleParameterRepository.findByRuleId(rule.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                TenantRuleParameter::getParamKey,
                                TenantRuleParameter::getParamValue
                        ));

        //get evaluator object from factory
        RuleEvaluator evaluator= ruleFactory.getRuleEvaluator(rule.getRuleCode());

        //build context to give it to evaluate
        RuleContext context=new RuleContext(rule,params);

        evaluator.evaluate(context);
    }
}
