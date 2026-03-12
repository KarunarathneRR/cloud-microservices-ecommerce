package com.ecommerce.userservice.service;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UpdateUserRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.exception.DuplicateEmailException;
import com.ecommerce.userservice.exception.ForbiddenOperationException;
import com.ecommerce.userservice.exception.InvalidCredentialsException;
import com.ecommerce.userservice.exception.UserNotFoundException;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtil;

import lombok.RequiredArgsConstructor;

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
        
        user.setRole(normalizeRole(request.getRole()));

        // Save user to database
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Return user response (without password)
        return mapToResponse(savedUser);
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

        if (!user.isActive()) {
            logger.warn("Login failed: Inactive account - {}", request.getEmail());
            throw new InvalidCredentialsException("Account is deactivated");
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
        return mapToResponse(user);
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
        return mapToResponse(user);
    }

    public List<UserResponse> getUsers(String role) {
        logger.info("Fetching users with role filter: {}", role);

        return userRepository.findAll()
                .stream()
                .filter(user -> role == null || role.isBlank() || user.getRole().equalsIgnoreCase(role))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long targetUserId, Long requesterUserId, boolean isAdmin, UpdateUserRequest request) {
        logger.info("Updating user profile targetUserId={}, requesterUserId={}, isAdmin={}",
                targetUserId, requesterUserId, isAdmin);

        if (!isAdmin && !targetUserId.equals(requesterUserId)) {
            throw new ForbiddenOperationException("You can only update your own profile");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + targetUserId));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse deactivateUser(Long targetUserId, Long requesterUserId, boolean isAdmin) {
        logger.info("Deactivating user targetUserId={}, requesterUserId={}, isAdmin={}",
                targetUserId, requesterUserId, isAdmin);

        if (!isAdmin && !targetUserId.equals(requesterUserId)) {
            throw new ForbiddenOperationException("You can only deactivate your own profile");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + targetUserId));

        user.setActive(false);
        return mapToResponse(userRepository.save(user));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive()
        );
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CUSTOMER";
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(normalizedRole)) {
            return "ADMIN";
        }

        if ("USER".equals(normalizedRole) || "CUSTOMER".equals(normalizedRole)) {
            return "CUSTOMER";
        }

        return "CUSTOMER";
    }
}
