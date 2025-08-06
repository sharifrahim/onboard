package com.github.sharifrahim.onboard.exception;

/**
 * Exception thrown when validation fails in the onboarding process
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
