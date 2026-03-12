package com.ecommerce.paymentservice.client;

import com.ecommerce.paymentservice.dto.UserResponse;
import com.ecommerce.paymentservice.exception.UserServiceException;
import com.ecommerce.paymentservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * UserServiceClient
 * HTTP client responsible for communicating with the user-service
 * Implements the payment → user inter-service communication leg
 *
 * Called during payment processing to verify that the provided userId
 * corresponds to an existing user before charging them.
 */
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestTemplate restTemplate;

    /**
     * Base URL of the user-service, injected from application.properties
     * Default: http://localhost:8081
     */
    @Value("${user.service.url}")
    private String userServiceUrl;

    /**
     * Fetch a user from the user-service by their ID
     * Used to validate that a user exists before processing their payment
     *
     * @param userId the user's unique identifier
     * @return UserResponse containing user details from user-service
     * @throws UserNotFoundException    if user-service returns 404
     * @throws UserServiceException     if user-service is unavailable or returns an error
     */
    public UserResponse getUserById(Long userId) {
        String url = userServiceUrl + "/users/internal/" + userId;
        logger.info("Calling user-service to validate user ID: {} at URL: {}", userId, url);

        try {
            UserResponse user = restTemplate.getForObject(url, UserResponse.class);
            logger.info("User validation successful — id={}, email={}", userId, user != null ? user.getEmail() : "null");
            return user;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("User not found in user-service — userId={}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        } catch (ResourceAccessException e) {
            logger.error("Could not reach user-service at {}: {}", url, e.getMessage());
            throw new UserServiceException("User service is unavailable. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error while calling user-service: {}", e.getMessage());
            throw new UserServiceException("Failed to communicate with user service: " + e.getMessage());
        }
    }
}
