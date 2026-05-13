package com.tss.AmlSystem.exception;

/**
 * Thrown when a user attempts to access a resource or perform an action they are not authorized for.
 * This maps to a 403 Forbidden status.
 */
public class UnauthorizedAccessException extends BaseAmlException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
