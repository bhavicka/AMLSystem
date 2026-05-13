package com.tss.AmlSystem.exception;

/**
 * Base abstract exception for the AML System.
 * All custom exceptions that are safe to expose to the frontend should extend this class.
 */
public abstract class BaseAmlException extends RuntimeException {
    public BaseAmlException(String message) {
        super(message);
    }

    public BaseAmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
