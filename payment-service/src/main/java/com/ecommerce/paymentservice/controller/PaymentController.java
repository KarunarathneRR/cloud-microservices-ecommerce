package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.exception.ForbiddenOperationException;
import com.ecommerce.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController
 * REST API endpoints for payment processing and retrieval
 *
 * Base path: /payments
 * Port: 8083
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for payment processing and retrieval")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a new payment
     * POST /payments
     *
     * Validates the user via user-service, then processes and stores the payment.
     *
     * @param request PaymentRequest body
     * @return ResponseEntity with PaymentResponse and HTTP 201 (CREATED)
     */
    @PostMapping
    @Operation(
            summary = "Process a payment",
            description = "Validates the user against user-service, then processes the payment for the given order. " +
                          "Returns the created payment record with COMPLETED status on success."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or validation error"),
            @ApiResponse(responseCode = "404", description = "User not found in user-service"),
            @ApiResponse(responseCode = "503", description = "User service unavailable")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        Long requesterUserId = getRequesterUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        if (isAdmin) {
            throw new ForbiddenOperationException("Admin users do not make payments");
        }
        if (!requesterUserId.equals(request.getUserId())) {
            throw new ForbiddenOperationException("You can only create payments for your own account");
        }
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get payment by ID
     * GET /payments/{id}
     *
     * @param id payment's unique identifier
     * @return ResponseEntity with PaymentResponse and HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get payment by ID",
            description = "Retrieves a single payment record by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found and returned"),
            @ApiResponse(responseCode = "404", description = "Payment not found with the given ID")
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * Get all payments for a specific order
     * GET /payments/order/{orderId}
     *
     * @param orderId the order's unique identifier
     * @return ResponseEntity with list of PaymentResponse and HTTP 200 (OK)
     */
    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Get payments by order ID",
            description = "Retrieves all payment records associated with a specific order"
    )
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }

    /**
     * Get all payments made by a specific user
     * GET /payments/user/{userId}
     *
     * @param userId the user's unique identifier
     * @return ResponseEntity with list of PaymentResponse and HTTP 200 (OK)
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get payments by user ID",
            description = "Retrieves all payment records made by a specific user"
    )
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long userId,
            Authentication authentication) {
        Long requesterUserId = getRequesterUserId(authentication);
        if (!isAdmin(authentication) && !requesterUserId.equals(userId)) {
            throw new ForbiddenOperationException("You can only view your own payment history");
        }
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @GetMapping
    @Operation(
            summary = "Get all payments",
            description = "Retrieves every payment record. Intended for admin reporting and management views."
    )
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentResponse>> getAllPayments(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new ForbiddenOperationException("Only admin users can view all payments");
        }
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private Long getRequesterUserId(Authentication authentication) {
        return (Long) authentication.getDetails();
    }

    /**
     * Health check endpoint
     * GET /payments/health
     *
     * @return ResponseEntity with health status message
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Simple endpoint to verify that the payment service is running"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running!");
    }
}
