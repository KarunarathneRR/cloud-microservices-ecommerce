package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController Class
 * REST API endpoints for payment processing and querying
 * 
 * MICROSERVICES ARCHITECTURE:
 * This controller serves as the main interface for Order Service to:
 * 1. Process payments (POST /payments/process)
 * 2. Check payment status (GET /payments/order/{orderId})
 * 
 * TYPICAL WORKFLOW:
 * 1. User places order → Order Service creates order
 * 2. Order Service calls POST /payments/process with order details
 * 3. Payment Service processes payment and returns status
 * 4. Order Service updates order status based on payment result
 * 5. If needed, Order Service can query payment status via GET /payments/order/{orderId}
 * 
 * In production, consider:
 * - Adding authentication/authorization (API keys, OAuth)
 * - Implementing retry logic for failed payments
 * - Adding circuit breaker pattern (Resilience4j)
 * - Using message queues (RabbitMQ, Kafka) for async processing
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for payment processing and status checking")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a payment
     * POST /payments/process
     * 
     * MICROSERVICES INTEGRATION:
     * - Called by Order Service after order creation
     * - Order Service sends order ID, amount, and payment method
     * - Returns payment status for order fulfillment decision
     * 
     * @param request PaymentRequest containing order details
     * @return ResponseEntity with PaymentResponse and HTTP 201 (CREATED)
     */
    @PostMapping("/process")
    @Operation(
            summary = "Process a payment",
            description = "Processes a payment request from Order Service. Simulates payment gateway interaction and returns payment status (SUCCESS/FAILED). Order Service uses this status to determine order fulfillment."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment processed (check status field for SUCCESS/FAILED)"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicate payment - payment already exists for this order"),
            @ApiResponse(responseCode = "500", description = "Payment processing error")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get payment by ID
     * GET /payments/{id}
     * 
     * @param id payment ID
     * @return ResponseEntity with PaymentResponse and HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get payment by ID",
            description = "Retrieves payment details by payment ID. Returns payment status, amount, method, and timestamp."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found and returned"),
            @ApiResponse(responseCode = "404", description = "Payment not found with the given ID")
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by order ID
     * GET /payments/order/{orderId}
     * 
     * MICROSERVICES INTEGRATION:
     * - Called by Order Service to check payment status for an order
     * - Critical for order fulfillment workflow
     * - If payment status is SUCCESS, Order Service proceeds with fulfillment
     * - If payment status is FAILED, Order Service cancels order or prompts for new payment
     * 
     * @param orderId order ID from Order Service
     * @return ResponseEntity with PaymentResponse and HTTP 200 (OK)
     */
    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Get payment by order ID",
            description = "Retrieves payment details by order ID. Used by Order Service to check payment status before order fulfillment. Essential for microservices integration and order workflow."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found for the order"),
            @ApiResponse(responseCode = "404", description = "Payment not found for the given order ID")
    })
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @Parameter(description = "Order ID from Order Service", example = "1001")
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * GET /payments/health
     * 
     * Used by service discovery and monitoring tools to check service health
     * 
     * @return ResponseEntity with health status message
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Simple endpoint to check if the payment service is running. Used by monitoring tools and service discovery."
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running!");
    }
}
