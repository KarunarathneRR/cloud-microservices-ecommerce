package com.ecommerce.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PaymentResponse DTO
 * Data Transfer Object for payment responses
 * Returned to Order Service after payment processing
 * 
 * MICROSERVICES INTEGRATION:
 * - Returned by POST /payments/process after payment attempt
 * - Order Service uses this to update order status
 * - Contains payment status (SUCCESS/FAILED) for order fulfillment decisions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * Payment ID (database primary key)
     */
    private Long id;

    /**
     * Order ID from Order Service
     */
    private Long orderId;

    /**
     * Payment amount
     */
    private Double amount;

    /**
     * Payment method used
     */
    private String method;

    /**
     * Payment status: "PENDING", "SUCCESS", or "FAILED"
     * Order Service checks this to determine next steps:
     * - SUCCESS: Proceed with order fulfillment
     * - FAILED: Cancel order or prompt user for alternative payment
     */
    private String status;

    /**
     * Timestamp when payment was created
     */
    private LocalDateTime createdAt;

    /**
     * Message describing payment result
     */
    private String message;

    /**
     * Convenience constructor without message
     */
    public PaymentResponse(Long id, Long orderId, Double amount, String method, String status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
    }
}
