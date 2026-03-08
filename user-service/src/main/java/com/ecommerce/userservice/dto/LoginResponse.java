package com.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO
 * Data Transfer Object for successful login responses
 * Contains JWT token and user information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT token for authentication
     * Should be included in subsequent requests as: Authorization: Bearer <token>
     */
    private String token;

    /**
     * User's unique identifier
     */
    private Long userId;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's role in the system
     */
    private String role;

    /**
     * Success message
     */
    private String message;

    /**
     * Convenience constructor without message
     */
    public LoginResponse(String token, Long userId, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.message = "Login successful";
    }
}
