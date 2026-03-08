package com.ecommerce.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig Class
 * Spring Security configuration for the user service
 * Configures password encoding and disables default security for public endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure BCrypt password encoder
     * BCrypt is a strong hashing algorithm suitable for password storage
     * Uses default strength (10 rounds)
     * 
     * @return PasswordEncoder bean for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure HTTP Security
     * Disables CSRF (not needed for stateless REST API)
     * Disables default login form
     * Allows all requests without authentication
     * Sets stateless session management
     * 
     * Note: This is a simplified configuration suitable for a university assignment
     * In production, you would implement proper JWT-based authentication filters
     * and role-based access control
     * 
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain bean
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (not needed for stateless REST APIs)
                .csrf(csrf -> csrf.disable())
                
                // Allow all requests without authentication
                // JWT validation is handled manually in the service layer
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                
                // Disable form login (we use JWT tokens, not sessions)
                .formLogin(form -> form.disable())
                
                // Disable HTTP Basic authentication
                .httpBasic(basic -> basic.disable())
                
                // Set stateless session management (no server-side sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
