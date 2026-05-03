package com.tss.AmlSystem.exception;

/**
 * Thrown when a business logic validation fails.
 * This maps to a 400 Bad Request status.
 */
public class BusinessValidationException extends BaseAmlException {
    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
