package com.ecommerce.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity
 * Represents a user in the e-commerce system
 * This entity is mapped to the 'users' table in the database
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary key, auto-generated
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's full name
     */
    @Column(nullable = false)
    private String name;

    /**
     * User's email address (unique identifier)
     * Must be unique across all users
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User's hashed password
     * Never store plain text passwords!
     * This field is hashed using BCrypt before storage
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's role in the system
     * Examples: "USER", "ADMIN", "SELLER"
     * Default value is "USER"
     */
    @Column(nullable = false)
    private String role = "USER";

    /**
     * Whether the account is active and allowed to authenticate.
     */
    @Column(nullable = false)
    private boolean active = true;
}
