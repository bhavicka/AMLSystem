package com.tss.AmlSystem.utils;

public class UniqueNumberGenerator {
    public static String generateIdentifierNumber(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(System.currentTimeMillis());
        return sb.toString();
    }
}
