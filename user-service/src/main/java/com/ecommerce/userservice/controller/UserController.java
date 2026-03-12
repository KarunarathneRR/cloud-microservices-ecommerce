package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UpdateUserRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController Class
 * REST API endpoints for user management and authentication
 * Provides endpoints for registration, login, and user profile retrieval
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {

    private final UserService userService;

    /**
     * Register a new user
     * POST /users/register
     * 
     * @param request RegisterRequest containing user details
     * @return ResponseEntity with UserResponse and HTTP 201 (CREATED)
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details. Email must be unique. Password will be hashed before storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or validation error"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * User login
     * POST /users/login
     * 
     * @param request LoginRequest containing email and password
     * @return ResponseEntity with LoginResponse including JWT token and HTTP 200 (OK)
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns a JWT token for subsequent API calls"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID
     * GET /users/{id}
     * 
     * @param id user's unique identifier
     * @return ResponseEntity with UserResponse and HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves user profile information by user ID. Password is excluded from the response."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and returned"),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Get users",
            description = "Retrieves all users, optionally filtered by role. Intended for admin management views."
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getUsers(role));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user profile",
            description = "Updates mutable user profile fields. ID and role cannot be changed through this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to update another user's profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        Long requesterUserId = (Long) authentication.getDetails();
        return ResponseEntity.ok(userService.updateUser(id, requesterUserId, isAdmin, request));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(
            summary = "Deactivate user account",
            description = "Deactivates the specified account. ID and role remain unchanged; deactivated accounts cannot log in."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden to deactivate another user's profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> deactivateUser(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        Long requesterUserId = (Long) authentication.getDetails();
        return ResponseEntity.ok(userService.deactivateUser(id, requesterUserId, isAdmin));
    }

    /**
     * Internal: Get user by ID (no auth required)
     * GET /users/internal/{id}
     * Intended for inter-service communication (e.g. payment-service validating a user).
     * Not exposed publicly in the Swagger UI with the same constraints.
     *
     * @param id user's unique identifier
     * @return ResponseEntity with UserResponse and HTTP 200 (OK)
     */
    @GetMapping("/internal/{id}")
    @Operation(
            summary = "Get user by ID (internal)",
            description = "Internal endpoint for inter-service calls. No JWT required. Used by payment-service to validate a user before processing a payment."
    )
    @ApiResponse(responseCode = "200", description = "User found and returned")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUserByIdInternal(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Get user by email
     * GET /users/email/{email}
     * Used by other microservices to validate user existence
     * 
     * @param email user's email address
     * @return ResponseEntity with UserResponse and HTTP 200 (OK)
     */
    @GetMapping("/email/{email}")
    @Operation(
            summary = "Get user by email",
            description = "Retrieves user profile information by email address. Used by other microservices to validate user existence."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found and returned"),
            @ApiResponse(responseCode = "404", description = "User not found with the given email")
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "User email address", example = "john.doe@example.com")
            @PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * GET /users/health
     * 
     * @return ResponseEntity with health status message
     */
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Simple endpoint to check if the user service is running"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running!");
    }
}
