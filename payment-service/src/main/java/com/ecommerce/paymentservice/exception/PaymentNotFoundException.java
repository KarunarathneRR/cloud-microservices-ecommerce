package com.ecommerce.paymentservice.exception;

/**
 * PaymentNotFoundException
 * Thrown when a payment record cannot be found by its ID
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
