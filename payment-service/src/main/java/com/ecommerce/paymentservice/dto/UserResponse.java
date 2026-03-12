package com.ecommerce.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserResponse DTO
 * Maps the response received from the user-service REST API
 * Used by UserServiceClient when validating a user before processing payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * User's unique identifier
     */
    private Long id;

    /**
     * User's full name
     */
    private String name;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's role in the system (e.g., USER, ADMIN)
     */
    private String role;
}
