package com.tss.AmlSystem.exception;

/**
 * Thrown when a resource already exists (e.g., duplicate email, duplicate file).
 * This maps to a 409 Conflict status.
 */
public class DuplicateResourceException extends BaseAmlException {
    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
