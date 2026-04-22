package com.tss.AmlSystem.strategy;

import com.tss.AmlSystem.models.RuleContext;

public interface RuleEvaluator {
    void evaluate(RuleContext ruleContext);
}
