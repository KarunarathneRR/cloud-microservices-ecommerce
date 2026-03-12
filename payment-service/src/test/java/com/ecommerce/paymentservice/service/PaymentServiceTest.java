package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.client.UserServiceClient;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.UserResponse;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.exception.UserNotFoundException;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;
    private PaymentRequest paymentRequest;
    private UserResponse testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserResponse(1L, "John Doe", "john@example.com", "USER");
        paymentRequest = new PaymentRequest(1L, 1L, new BigDecimal("99.99"), "CREDIT_CARD");

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setOrderId(1L);
        testPayment.setUserId(1L);
        testPayment.setAmount(new BigDecimal("99.99"));
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setTransactionId("test-transaction-uuid");
        testPayment.setCreatedAt(LocalDateTime.now());
    }

    // ─── processPayment ──────────────────────────────────────────────────────

    @Test
    void processPayment_Success() {
        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());
        assertEquals(new BigDecimal("99.99"), response.getAmount());
        assertEquals("CREDIT_CARD", response.getPaymentMethod());
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getUserId());
        assertEquals("Payment processed successfully", response.getMessage());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_UserNotFound_ThrowsException() {
        when(userServiceClient.getUserById(anyLong()))
                .thenThrow(new UserNotFoundException("User not found with ID: 1"));

        assertThrows(UserNotFoundException.class,
                () -> paymentService.processPayment(paymentRequest));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void processPayment_TransactionIdIsGenerated() {
        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertNotNull(response.getTransactionId());
        assertFalse(response.getTransactionId().isBlank());
    }

    // ─── getPaymentById ──────────────────────────────────────────────────────

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());
        assertEquals(new BigDecimal("99.99"), response.getAmount());
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(999L));
    }

    // ─── getPaymentsByUserId ─────────────────────────────────────────────────

    @Test
    void getPaymentsByUserId_ReturnsPayments() {
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(testPayment));

        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getUserId());
        assertEquals(PaymentStatus.COMPLETED, responses.get(0).getStatus());
    }

    @Test
    void getPaymentsByUserId_ReturnsEmptyList_WhenNoPayments() {
        when(paymentRepository.findByUserId(999L)).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(999L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // ─── getPaymentsByOrderId ─────────────────────────────────────────────────

    @Test
    void getPaymentsByOrderId_ReturnsPayments() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(testPayment));

        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getOrderId());
    }

    @Test
    void getPaymentsByOrderId_ReturnsEmptyList_WhenNoPayments() {
        when(paymentRepository.findByOrderId(999L)).thenReturn(List.of());

        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(999L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void getAllPayments_ReturnsPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(testPayment));

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }
}
