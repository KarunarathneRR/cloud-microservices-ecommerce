package com.ecommerce.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserResponse DTO
 * Data Transfer Object for user information responses
 * Excludes sensitive information like password
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
     * User's role in the system
     */
    private String role;

    /**
     * Whether the account is currently active.
     */
    private boolean active;

    /**
     * Note: Password is intentionally excluded from this response
     * for security reasons. Never expose passwords in API responses!
     */
}
