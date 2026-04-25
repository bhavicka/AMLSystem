package com.tss.AmlSystem.dto.request;

import java.util.List;

public record RuleAssignmentDto (
        String schemaName,
        List<String> ruleCodes
){}

