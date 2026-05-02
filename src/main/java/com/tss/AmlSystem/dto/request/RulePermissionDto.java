package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record RulePermissionDto (
        @NotBlank(message = "Schema name is required")
        @Pattern(regexp = "^[a-z0-9_]+$", message = "Invalid schema name format")
        String schemaName,

        @NotEmpty(message = "At least one rule code must be provided")
        List<String> ruleCodes
){}

