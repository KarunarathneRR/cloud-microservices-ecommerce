package com.ecommerce.paymentservice.exception;

/**
 * UserNotFoundException
 * Thrown when the user-service returns 404 for the requested userId
 * Indicates the user does not exist, so the payment cannot be processed
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
