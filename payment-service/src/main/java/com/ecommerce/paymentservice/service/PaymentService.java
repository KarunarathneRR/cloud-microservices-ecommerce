package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.exception.DuplicatePaymentException;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.exception.PaymentProcessingException;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * PaymentService Class
 * Business logic layer for payment processing operations
 * Handles payment gateway simulation and transaction management
 * 
 * MICROSERVICES INTEGRATION:
 * - Processes payment requests from Order Service
 * - Returns payment status for order fulfillment workflow
 * - Stores payment history for audit and reconciliation
 * - In production, would integrate with real payment gateway (Stripe, PayPal, etc.)
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @Value("${payment.gateway.delay:1000}")
    private long gatewayDelay;

    @Value("${payment.gateway.failure-rate:0.2}")
    private double failureRate;

    /**
     * Process a payment request
     * Simulates payment gateway processing with configurable delay and failure rate
     * 
     * WORKFLOW:
     * 1. Validate no duplicate payment for this order
     * 2. Create pending payment record
     * 3. Simulate payment gateway call
     * 4. Update payment status based on gateway response
     * 5. Return payment response to Order Service
     * 
     * @param request PaymentRequest containing order ID, amount, and method
     * @return PaymentResponse with payment status
     * @throws DuplicatePaymentException if payment already exists for this order
     * @throws PaymentProcessingException if payment processing encounters technical error
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment for order ID: {}", request.getOrderId());

        // Check for duplicate payment
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            logger.warn("Duplicate payment attempt for order ID: {}", request.getOrderId());
            throw new DuplicatePaymentException(
                    "Payment already exists for order ID: " + request.getOrderId()
            );
        }

        // Create payment record with PENDING status
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus("PENDING");
        
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Created pending payment with ID: {}", savedPayment.getId());

        try {
            // Simulate payment gateway processing
            String gatewayResult = simulatePaymentGateway(request);
            
            // Update payment status based on gateway result
            savedPayment.setStatus(gatewayResult);
            savedPayment = paymentRepository.save(savedPayment);
            
            logger.info("Payment {} for order ID: {}", gatewayResult, request.getOrderId());

            // Return payment response
            String message = gatewayResult.equals("SUCCESS") 
                    ? "Payment processed successfully" 
                    : "Payment failed - please try again or use different payment method";

            return new PaymentResponse(
                    savedPayment.getId(),
                    savedPayment.getOrderId(),
                    savedPayment.getAmount(),
                    savedPayment.getMethod(),
                    savedPayment.getStatus(),
                    savedPayment.getCreatedAt(),
                    message
            );

        } catch (Exception e) {
            // Update payment status to FAILED on exception
            savedPayment.setStatus("FAILED");
            paymentRepository.save(savedPayment);
            
            logger.error("Payment processing failed for order ID: {}", request.getOrderId(), e);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Simulate payment gateway processing
     * In production, this would be replaced with actual payment gateway API calls
     * (e.g., Stripe, PayPal, Braintree, etc.)
     * 
     * SIMULATION LOGIC:
     * - Introduces configurable delay (simulates network latency)
     * - Randomly returns SUCCESS or FAILED based on failure rate
     * - In production, actual gateway would validate card, check funds, etc.
     * 
     * @param request PaymentRequest
     * @return "SUCCESS" or "FAILED"
     */
    private String simulatePaymentGateway(PaymentRequest request) {
        logger.info("Calling payment gateway for order ID: {} with method: {}", 
                request.getOrderId(), request.getMethod());

        try {
            // Simulate network delay
            Thread.sleep(gatewayDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Payment gateway simulation interrupted");
        }

        // Simulate random success/failure based on configured failure rate
        boolean isSuccess = random.nextDouble() > failureRate;
        String result = isSuccess ? "SUCCESS" : "FAILED";
        
        logger.info("Payment gateway returned: {} for order ID: {}", result, request.getOrderId());
        return result;
    }

    /**
     * Get payment by ID
     * Used to retrieve payment details
     * 
     * @param id payment ID
     * @return PaymentResponse
     * @throws PaymentNotFoundException if payment not found
     */
    public PaymentResponse getPaymentById(Long id) {
        logger.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Payment not found with ID: {}", id);
                    return new PaymentNotFoundException("Payment not found with ID: " + id);
                });

        logger.info("Payment found: ID={}, Status={}", payment.getId(), payment.getStatus());

        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    /**
     * Get payment by order ID
     * Used by Order Service to check payment status for an order
     * Critical for order fulfillment workflow
     * 
     * MICROSERVICES INTEGRATION:
     * - Order Service calls GET /payments/order/{orderId}
     * - Uses payment status to decide whether to fulfill order
     * - SUCCESS status → proceed with order fulfillment
     * - FAILED status → cancel order or prompt for new payment
     * 
     * @param orderId order ID from Order Service
     * @return PaymentResponse
     * @throws PaymentNotFoundException if payment not found for this order
     */
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        logger.info("Fetching payment by order ID: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    logger.warn("Payment not found for order ID: {}", orderId);
                    return new PaymentNotFoundException("Payment not found for order ID: " + orderId);
                });

        logger.info("Payment found for order ID {}: Status={}", orderId, payment.getStatus());

        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
