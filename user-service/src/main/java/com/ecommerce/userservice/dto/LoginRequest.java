package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest DTO
 * Data Transfer Object for user login requests
 * Contains user credentials for authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * User's email address
     * Used as the unique identifier for login
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * User's password
     * Plain text password that will be verified against hashed password in database
     */
    @NotBlank(message = "Password is required")
    private String password;
}
