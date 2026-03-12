package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.client.UserServiceClient;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.UserResponse;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PaymentService
 * Business logic layer for payment operations
 *
 * Flow (payment → user):
 *   1. Receive payment request with orderId, userId, amount, paymentMethod
 *   2. Call user-service to verify the userId exists
 *   3. Create payment record with PENDING status
 *   4. Simulate payment gateway processing
 *   5. Update status to COMPLETED and persist
 *   6. Return PaymentResponse
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final UserServiceClient userServiceClient;

    /**
     * Process a new payment
     * Validates the user via user-service, then processes and persists the payment
     *
     * @param request PaymentRequest containing orderId, userId, amount, paymentMethod
     * @return PaymentResponse with full payment details and status
     * @throws com.ecommerce.paymentservice.exception.UserNotFoundException if userId does not exist in user-service
     * @throws com.ecommerce.paymentservice.exception.UserServiceException  if user-service is unreachable
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        logger.info("Processing payment — orderId={}, userId={}, amount={}",
                request.getOrderId(), request.getUserId(), request.getAmount());

        // Step 1: Validate user via user-service (payment → user)
        UserResponse user = userServiceClient.getUserById(request.getUserId());
        logger.info("User validated — name={}, email={}", user.getName(), user.getEmail());

        // Step 2: Build payment record with PENDING status
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString());

        // Step 3: Simulate payment gateway processing
        // In production this would call Stripe / PayPal / bank gateway
        payment.setStatus(PaymentStatus.COMPLETED);

        // Step 4: Persist
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment completed — paymentId={}, transactionId={}",
                savedPayment.getId(), savedPayment.getTransactionId());

        return mapToResponse(savedPayment, "Payment processed successfully");
    }

    /**
     * Retrieve a single payment by its ID
     *
     * @param id payment's unique identifier
     * @return PaymentResponse
     * @throws PaymentNotFoundException if no payment exists with the given ID
     */
    public PaymentResponse getPaymentById(Long id) {
        logger.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Payment not found — id={}", id);
                    return new PaymentNotFoundException("Payment not found with ID: " + id);
                });

        return mapToResponse(payment, null);
    }

    /**
     * Retrieve all payments associated with a given order
     *
     * @param orderId the order's unique identifier
     * @return list of PaymentResponse objects
     */
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        logger.info("Fetching payments for orderId: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(p -> mapToResponse(p, null))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all payments made by a given user
     *
     * @param userId the user's unique identifier
     * @return list of PaymentResponse objects
     */
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        logger.info("Fetching payments for userId: {}", userId);
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(p -> mapToResponse(p, null))
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments() {
        logger.info("Fetching all payments");
        return paymentRepository.findAll()
                .stream()
                .map(payment -> mapToResponse(payment, null))
                .collect(Collectors.toList());
    }

    /**
     * Map a Payment entity to a PaymentResponse DTO
     *
     * @param payment the entity to map
     * @param message optional human-readable message (null for query results)
     * @return PaymentResponse
     */
    private PaymentResponse mapToResponse(Payment payment, String message) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getCreatedAt(),
                message
        );
    }
}
