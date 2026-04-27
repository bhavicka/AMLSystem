package com.tss.AmlSystem.entity.enums;
import lombok.Getter;

@Getter
public enum LogTag {
    AUTH("[AUTH]"),
    TENANT("[TENANT]"),
    RULE("[RULE]"),
    BATCH("[BATCH]"),
    SYSTEM("[SYSTEM]"),
    SECURITY("[SECURITY]"),
    EMAIL("[EMAIL]");

    private final String value;

    LogTag(String value) {
        this.value = value;
    }
}
