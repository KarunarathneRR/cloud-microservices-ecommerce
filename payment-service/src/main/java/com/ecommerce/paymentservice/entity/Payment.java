package com.ecommerce.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity
 * Represents a payment transaction in the e-commerce system
 * Mapped to the 'payments' table in the H2 in-memory database
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    /**
     * Primary key, auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The order ID this payment belongs to (from order-service)
     * Payment-service does not own the Order entity; it stores the reference only
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * The user who made this payment (validated via user-service)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Payment amount (e.g., 99.99)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Current status of the payment
     * Stored as a string in the database for readability
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Payment method used (e.g., CREDIT_CARD, DEBIT_CARD, PAYPAL)
     */
    @Column(nullable = false)
    private String paymentMethod;

    /**
     * Unique transaction ID generated at payment time
     * Used to trace the payment in external payment gateways
     */
    @Column(unique = true)
    private String transactionId;

    /**
     * Timestamp when the payment record was created
     * Set automatically before persisting
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically set createdAt before the entity is first persisted
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
