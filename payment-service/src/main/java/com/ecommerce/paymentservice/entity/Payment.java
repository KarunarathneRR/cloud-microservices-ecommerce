package com.ecommerce.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payment Entity
 * Represents a payment transaction in the e-commerce system
 * This entity is mapped to the 'payments' table in the database
 * 
 * MICROSERVICES INTEGRATION:
 * - orderId links this payment to an order in the Order Service
 * - Order Service calls POST /payments/process to create a payment
 * - Order Service can query payment status via GET /payments/order/{orderId}
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
     * Order ID from Order Service
     * Links this payment to an order in another microservice
     * In a real system, this could be validated via REST call to Order Service
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * Payment amount in dollars
     * Should match the order total from Order Service
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * Payment method
     * Examples: "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"
     */
    @Column(nullable = false)
    private String method;

    /**
     * Payment status
     * Values: "PENDING", "SUCCESS", "FAILED"
     * - PENDING: Payment is being processed
     * - SUCCESS: Payment completed successfully
     * - FAILED: Payment failed (insufficient funds, invalid card, etc.)
     */
    @Column(nullable = false)
    private String status;

    /**
     * Timestamp when payment was created
     * Automatically set when payment record is created
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Pre-persist hook to set createdAt timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
