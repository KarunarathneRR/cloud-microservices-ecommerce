package com.ecommerce.paymentservice.exception;

/**
 * PaymentProcessingException
 * Custom exception thrown when payment processing fails due to gateway errors
 * Represents technical failures in payment gateway communication
 */
public class PaymentProcessingException extends RuntimeException {

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public PaymentProcessingException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the underlying cause
     */
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
