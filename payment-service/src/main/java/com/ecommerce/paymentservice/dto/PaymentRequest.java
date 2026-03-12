package com.ecommerce.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PaymentRequest DTO
 * Data Transfer Object for initiating a payment
 * Sent by the caller (order-service or client) to create a new payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /**
     * The order ID this payment is for
     * References the order in order-service (not validated locally)
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * The ID of the user making the payment
     * This is validated against user-service before processing
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * The total amount to be charged
     * Must be greater than zero
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Payment method chosen by the user
     * Examples: CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
