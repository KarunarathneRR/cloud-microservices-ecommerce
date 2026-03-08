package com.ecommerce.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentRequest DTO
 * Data Transfer Object for payment processing requests
 * Sent by Order Service when an order needs to be paid
 * 
 * MICROSERVICES INTEGRATION:
 * - Order Service sends this DTO to POST /payments/process
 * - Contains order ID to link payment with order
 * - Amount should match order total from Order Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /**
     * Order ID from Order Service
     * Links this payment to an order
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Payment amount in dollars
     * Must be greater than 0
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    /**
     * Payment method
     * Examples: "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"
     */
    @NotBlank(message = "Payment method is required")
    private String method;
}
