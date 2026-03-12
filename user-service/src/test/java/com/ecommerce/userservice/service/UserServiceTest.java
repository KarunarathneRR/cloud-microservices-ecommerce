package com.ecommerce.userservice.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "John Doe", "john@example.com", "hashedPassword", "CUSTOMER", true);
        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123", "CUSTOMER");
        loginRequest = new LoginRequest("john@example.com", "password123");
    }

    // ─── registerUser ────────────────────────────────────────────────────────

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_NullRole_DefaultsToUser() {
        registerRequest.setRole(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        User savedUser = new User(1L, "John Doe", "john@example.com", "hashedPassword", "CUSTOMER", true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(registerRequest);

        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void registerUser_EmptyRole_DefaultsToUser() {
        registerRequest.setRole("");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        User savedUser = new User(1L, "John Doe", "john@example.com", "hashedPassword", "CUSTOMER", true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(registerRequest);

        assertEquals("CUSTOMER", response.getRole());
    }

    // ─── loginUser ───────────────────────────────────────────────────────────

    @Test
    void loginUser_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getEmail(), testUser.getId(), testUser.getRole()))
                .thenReturn("jwt-token");

        LoginResponse response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void loginUser_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
        verify(jwtUtil, never()).generateToken(anyString(), any(), anyString());
    }

    @Test
    void loginUser_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
        verify(jwtUtil, never()).generateToken(anyString(), any(), anyString());
    }

    // ─── getUserById ─────────────────────────────────────────────────────────

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getUsers_ReturnsFilteredUsers() {
        User admin = new User(2L, "Admin", "admin@example.com", "hashedPassword", "ADMIN", true);
        when(userRepository.findAll()).thenReturn(List.of(testUser, admin));

        List<UserResponse> responses = userService.getUsers("CUSTOMER");

        assertEquals(1, responses.size());
        assertEquals("CUSTOMER", responses.get(0).getRole());
    }

    @Test
    void updateUser_SelfUpdate_Success() {
        UpdateUserRequest request = new UpdateUserRequest("Jane Doe", "jane@example.com", "newpassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateUser(1L, 1L, false, request);

        assertEquals("Jane Doe", response.getName());
        assertEquals("jane@example.com", response.getEmail());
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void updateUser_AnotherUserWithoutAdmin_ThrowsForbidden() {
        UpdateUserRequest request = new UpdateUserRequest("Jane Doe", "jane@example.com", null);

        assertThrows(ForbiddenOperationException.class,
                () -> userService.updateUser(1L, 2L, false, request));
    }

    @Test
    void deactivateUser_SelfDeactivate_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.deactivateUser(1L, 1L, false);

        assertFalse(response.isActive());
    }

    @Test
    void loginUser_InactiveAccount_ThrowsException() {
        testUser.setActive(false);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser(loginRequest));
        assertEquals("Account is deactivated", ex.getMessage());
    }
}
