package com.tss.AmlSystem.strategy.rule;

import com.tss.AmlSystem.models.RuleContext;

public interface RuleEvaluator {
    void evaluate(RuleContext ruleContext);
}
