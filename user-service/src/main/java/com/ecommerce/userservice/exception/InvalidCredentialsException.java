package com.ecommerce.userservice.exception;

/**
 * InvalidCredentialsException
 * Custom exception thrown when login credentials are invalid
 * Used when email doesn't exist or password doesn't match
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
