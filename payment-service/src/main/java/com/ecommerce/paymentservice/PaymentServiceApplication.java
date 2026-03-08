package com.ecommerce.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Main Spring Boot application class for Payment Service.
 * This microservice handles payment processing for the e-commerce system.
 * 
 * Features:
 * - Payment processing with simulated payment gateway
 * - Order-payment linking and status tracking
 * - Duplicate payment prevention (idempotency)
 * - H2 in-memory database for development
 * - Swagger UI for API documentation
 * 
 * Microservices Architecture:
 * - Integrates with Order Service to process payments after order creation
 * - Exposes payment status endpoints for order fulfillment workflow
 * - Designed to be called by Order Service via REST API
 * 
 * Port: 8082
 * Context Path: /
 * 
 * @author CTSE Cloud Computing Assignment
 * @version 1.0
 */
@SpringBootApplication
public class PaymentServiceApplication {

    /**
     * Main entry point for the Payment Service application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    /**
     * Event listener that runs after the application context is fully initialized.
     * Displays useful URLs and information for developers.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void displayStartupBanner() {
        System.out.println("\n" +
                "======================================================================\n" +
                "  💳 PAYMENT SERVICE - Successfully Started!\n" +
                "======================================================================\n" +
                "\n" +
                "  🚀 Service URL:        http://localhost:8082\n" +
                "  📚 Swagger UI:         http://localhost:8082/swagger-ui.html\n" +
                "  📖 API Docs (JSON):    http://localhost:8082/v3/api-docs\n" +
                "  🗄️  H2 Database Console: http://localhost:8082/h2-console\n" +
                "\n" +
                "  Database Connection Details:\n" +
                "  ----------------------------\n" +
                "  JDBC URL:    jdbc:h2:mem:paymentdb\n" +
                "  Username:    sa\n" +
                "  Password:    (leave blank)\n" +
                "\n" +
                "  Payment Gateway Simulation:\n" +
                "  ---------------------------\n" +
                "  Delay:       1000ms (configurable in application.properties)\n" +
                "  Failure Rate: 20% (random failures for testing)\n" +
                "\n" +
                "  Microservices Integration:\n" +
                "  -------------------------\n" +
                "  This service is designed to be called by Order Service\n" +
                "  to process payments after order creation.\n" +
                "\n" +
                "  Key Endpoints:\n" +
                "  - POST   /payments/process         (Process new payment)\n" +
                "  - GET    /payments/{id}            (Get payment by ID)\n" +
                "  - GET    /payments/order/{orderId} (Get payment by Order ID)\n" +
                "  - GET    /payments/health          (Health check)\n" +
                "\n" +
                "  Example: Process a payment\n" +
                "  -------------------------\n" +
                "  POST http://localhost:8082/payments/process\n" +
                "  Body: {\n" +
                "    \"orderId\": 123,\n" +
                "    \"amount\": 299.99,\n" +
                "    \"method\": \"CREDIT_CARD\"\n" +
                "  }\n" +
                "\n" +
                "======================================================================\n" +
                "  ✅ Ready to accept payment requests from Order Service!\n" +
                "======================================================================\n");
    }
}
