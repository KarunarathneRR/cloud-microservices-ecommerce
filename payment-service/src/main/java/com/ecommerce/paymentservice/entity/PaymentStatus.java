package com.ecommerce.paymentservice.entity;

/**
 * PaymentStatus Enum
 * Represents the possible states of a payment transaction
 */
public enum PaymentStatus {

    /**
     * Payment has been initiated but not yet processed
     */
    PENDING,

    /**
     * Payment was successfully processed
     */
    COMPLETED,

    /**
     * Payment processing failed (e.g., insufficient funds, gateway error)
     */
    FAILED,

    /**
     * Payment was refunded after successful processing
     */
    REFUNDED
}
