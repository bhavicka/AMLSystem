package com.tss.AmlSystem.utils;

/**
 * Global constants used across the application to avoid magic numbers and strings.
 */
public final class GlobalConstants {

    private GlobalConstants() {
        // Private constructor to prevent instantiation
    }

    // Security & Authentication
    public static final int SECURE_PASSWORD_LENGTH = 12;
    public static final String PASSWORD_CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    
    // CORS Configuration
    public static final String ALLOWED_ORIGIN_DEV = "http://localhost:4200";

    // Hashing
    public static final String HASH_ALGORITHM_SHA256 = "SHA-256";
}
