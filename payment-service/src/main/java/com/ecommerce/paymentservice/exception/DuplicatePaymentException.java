package com.ecommerce.paymentservice.exception;

/**
 * DuplicatePaymentException
 * Custom exception thrown when attempting to process a payment for an order that already has a payment
 * Prevents duplicate charges for the same order
 * 
 * MICROSERVICES INTEGRATION:
 * - Protects against race conditions if Order Service sends duplicate payment requests
 * - Ensures idempotency in payment processing
 */
public class DuplicatePaymentException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public DuplicatePaymentException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public DuplicatePaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
