package com.tss.AmlSystem.exception;

/**
 * Thrown when a requested resource (e.g., User, Case, Rule) is not found.
 * This maps to a 404 Not Found status.
 */
public class ResourceNotFoundException extends BaseAmlException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
