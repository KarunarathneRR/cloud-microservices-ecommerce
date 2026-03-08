package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.exception.DuplicateEmailException;
import com.ecommerce.userservice.exception.InvalidCredentialsException;
import com.ecommerce.userservice.exception.UserNotFoundException;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService Class
 * Business logic layer for user management operations
 * Handles user registration, authentication, and profile retrieval
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user
     * Validates email uniqueness, hashes password, and saves user to database
     * 
     * @param request RegisterRequest containing user details
     * @return UserResponse with created user information (excluding password)
     * @throws DuplicateEmailException if email already exists
     */
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }

        // Create new user entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // Hash the password using BCrypt
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Set role (default to "USER" if not provided)
        user.setRole(request.getRole() != null && !request.getRole().isEmpty() 
                ? request.getRole() : "USER");

        // Save user to database
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Return user response (without password)
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    /**
     * Authenticate user and generate JWT token
     * Validates credentials and returns token for successful login
     * 
     * @param request LoginRequest containing email and password
     * @return LoginResponse with JWT token and user information
     * @throws InvalidCredentialsException if credentials are invalid
     */
    public LoginResponse loginUser(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found - {}", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Invalid password for user - {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        logger.info("User logged in successfully: {}", user.getEmail());

        // Return login response with token and user info
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                "Login successful"
        );
    }

    /**
     * Get user by ID
     * Retrieves user profile information
     * 
     * @param id user's unique identifier
     * @return UserResponse with user information (excluding password)
     * @throws UserNotFoundException if user doesn't exist
     */
    public UserResponse getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });

        logger.info("User found: {}", user.getEmail());

        // Return user response (without password)
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Get user by email
     * Used by other microservices to validate user existence
     * 
     * @param email user's email address
     * @return UserResponse with user information (excluding password)
     * @throws UserNotFoundException if user doesn't exist
     */
    public UserResponse getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        logger.info("User found with email: {}", email);

        // Return user response (without password)
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
