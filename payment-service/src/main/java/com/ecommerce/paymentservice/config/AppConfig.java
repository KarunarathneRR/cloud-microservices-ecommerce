package com.ecommerce.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AppConfig
 * Application-level Spring bean configuration
 * Provides a shared RestTemplate instance used by UserServiceClient
 * to make HTTP calls to the user-service
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate bean for synchronous HTTP communication
     * Used by UserServiceClient to call user-service endpoints
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
