package com.ecommerce.userservice.exception;

/**
 * UserNotFoundException
 * Custom exception thrown when a user is not found in the database
 * Used in scenarios like: retrieving user by ID or email that doesn't exist
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
