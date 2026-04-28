package com.tss.AmlSystem.entity.enums.tenant;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum FileType {
    CUSTOMERS,
    TRANSACTIONS,
    ACCOUNTS;

    @JsonCreator
    public static FileType fromString(String value) {
        try {
            return FileType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            // Return null so that @NotNull validation triggers
            return null;
        }
    }
}
