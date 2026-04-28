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
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleExecutionService {
    private final RuleFactory ruleFactory;
    private final TransactionRepository transactionRepository;
    private final TenantRuleParameterRepository tenantRuleParameterRepository;

    public void runRule(TenantRule rule){
        log.info("{} Initiating Rule Execution for: {}", LogTag.RULE.getValue(), rule.getRuleCode());
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

        log.debug("{} Evaluating transactions against rule: {}", LogTag.RULE.getValue(), rule.getRuleCode());
        evaluator.evaluate(context);
    }
}
