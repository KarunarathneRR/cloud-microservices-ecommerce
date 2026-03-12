package com.ecommerce.paymentservice.dto;

import com.ecommerce.paymentservice.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentResponse DTO
 * Data Transfer Object returned after a payment operation
 * Contains full payment details and a human-readable message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * Unique payment identifier
     */
    private Long id;

    /**
     * The order this payment belongs to
     */
    private Long orderId;

    /**
     * The user who made this payment
     */
    private Long userId;

    /**
     * Amount that was charged
     */
    private BigDecimal amount;

    /**
     * Current status of the payment (PENDING, COMPLETED, FAILED, REFUNDED)
     */
    private PaymentStatus status;

    /**
     * Payment method used
     */
    private String paymentMethod;

    /**
     * Unique transaction reference ID
     */
    private String transactionId;

    /**
     * Timestamp when the payment was created
     */
    private LocalDateTime createdAt;

    /**
     * Human-readable result message (e.g., "Payment processed successfully")
     */
    private String message;
}
