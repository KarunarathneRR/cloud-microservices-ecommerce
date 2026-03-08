package com.ecommerce.paymentservice.exception;

/**
 * PaymentNotFoundException
 * Custom exception thrown when a payment is not found in the database
 * Used when querying payments by ID or order ID that don't exist
 */
public class PaymentNotFoundException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public PaymentNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
