package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PaymentRepository Interface
 * Provides CRUD operations and custom queries for Payment entity
 * JpaRepository provides built-in methods: save, findById, findAll, delete, etc.
 * 
 * MICROSERVICES INTEGRATION:
 * - findByOrderId() allows Order Service to check payment status for a specific order
 * - This is crucial for order fulfillment workflow
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find a payment by order ID
     * Used by Order Service to check if payment exists for an order
     * 
     * @param orderId the order ID from Order Service
     * @return Optional containing the payment if found, empty if not found
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Check if a payment exists for a given order ID
     * Useful to prevent duplicate payment attempts for the same order
     * 
     * @param orderId the order ID to check
     * @return true if payment exists, false otherwise
     */
    boolean existsByOrderId(Long orderId);
}
