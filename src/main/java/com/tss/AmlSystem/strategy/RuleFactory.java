package com.tss.AmlSystem.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RuleFactory {
    private final Map<String,RuleEvaluator> ruleEvaluatorMap;

    public RuleEvaluator getRuleEvaluator(String ruleType){
        return ruleEvaluatorMap.get(ruleType);
    }
}
