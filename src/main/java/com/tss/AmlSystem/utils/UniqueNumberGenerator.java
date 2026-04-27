package com.tss.AmlSystem.utils;

public class UniqueNumberGenerator {
    public static String generateAlertNumber() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("AL-");

        for (int i = 0; i < 4; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
