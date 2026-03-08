package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository Interface
 * Provides CRUD operations and custom queries for User entity
 * JpaRepository provides built-in methods: save, findById, findAll, delete, etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address
     * Used for login validation and checking email uniqueness
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given email
     * Useful for validating email uniqueness during registration
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
