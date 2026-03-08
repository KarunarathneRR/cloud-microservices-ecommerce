package com.ecommerce.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UserServiceApplication
 * Main entry point for the User Service microservice
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Spring Boot auto-configuration
 * - @ComponentScan: Scans for components in the current package and sub-packages
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        
        System.out.println("\n========================================");
        System.out.println("   USER SERVICE STARTED SUCCESSFULLY   ");
        System.out.println("========================================");
        System.out.println("Server running on: http://localhost:8081");
        System.out.println("Swagger UI: http://localhost:8081/swagger-ui.html");
        System.out.println("H2 Console: http://localhost:8081/h2-console");
        System.out.println("  - JDBC URL: jdbc:h2:mem:userdb");
        System.out.println("  - Username: sa");
        System.out.println("  - Password: (leave blank)");
        System.out.println("========================================\n");
    }
}
