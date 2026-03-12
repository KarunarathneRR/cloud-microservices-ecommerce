package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PaymentRepository Interface
 * Provides CRUD operations and custom queries for Payment entity
 * JpaRepository provides built-in methods: save, findById, findAll, delete, etc.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find all payments associated with a specific order
     *
     * @param orderId the order's unique identifier
     * @return list of payments for the given order
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * Find all payments made by a specific user
     *
     * @param userId the user's unique identifier
     * @return list of payments made by the given user
     */
    List<Payment> findByUserId(Long userId);
}
