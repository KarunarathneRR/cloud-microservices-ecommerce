package com.ecommerce.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PaymentServiceApplication
 * Main entry point for the Payment Service microservice
 *
 * Responsibilities:
 *  - Accept payment requests (linked to an order and user)
 *  - Validate the user via user-service (payment → user)
 *  - Process and persist payment records
 *  - Expose payment query endpoints for downstream consumers
 */
@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);

        System.out.println("\n========================================");
        System.out.println("  PAYMENT SERVICE STARTED SUCCESSFULLY  ");
        System.out.println("========================================");
        System.out.println("Server running on: http://localhost:8083");
        System.out.println("Swagger UI: http://localhost:8083/swagger-ui.html");
        System.out.println("H2 Console: http://localhost:8083/h2-console");
        System.out.println("  - JDBC URL: jdbc:h2:mem:paymentdb");
        System.out.println("  - Username: sa");
        System.out.println("  - Password: (leave blank)");
        System.out.println("Depends on: user-service @ http://localhost:8081");
        System.out.println("========================================\n");
    }
}
