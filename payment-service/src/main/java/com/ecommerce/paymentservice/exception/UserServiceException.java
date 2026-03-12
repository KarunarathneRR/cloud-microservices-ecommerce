package com.ecommerce.paymentservice.exception;

/**
 * UserServiceException
 * Thrown when the user-service is unreachable or returns an unexpected error
 * Signals a downstream service failure — maps to 503 Service Unavailable
 */
public class UserServiceException extends RuntimeException {

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
