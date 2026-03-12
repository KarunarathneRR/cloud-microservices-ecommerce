package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest DTO
 * Data Transfer Object for user registration requests
 * Contains validation constraints to ensure data integrity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * User's full name
     * Must not be blank
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * User's email address
     * Must be a valid email format and not blank
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * User's password
     * Must be at least 6 characters long
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * User's role (optional)
     * If not provided, defaults to CUSTOMER in the service layer
     * Accepted values are CUSTOMER and ADMIN
     */
    private String role;
}
