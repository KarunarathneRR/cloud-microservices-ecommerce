package com.ecommerce.userservice.exception;

/**
 * DuplicateEmailException
 * Custom exception thrown when attempting to register a user with an email that already exists
 * Ensures email uniqueness constraint in the application layer
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public DuplicateEmailException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
