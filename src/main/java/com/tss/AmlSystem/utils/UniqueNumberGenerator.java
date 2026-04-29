package com.tss.AmlSystem.utils;

public class UniqueNumberGenerator {
    public static String generateIdentifierNumber(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        Long time=System.currentTimeMillis();
        sb.append(time.toString().substring(0,2));
        System.out.println(sb);
        return sb.toString();
    }
}
