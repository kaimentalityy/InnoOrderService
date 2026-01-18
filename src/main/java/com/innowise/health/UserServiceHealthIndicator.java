package com.innowise.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Custom health indicator to check User Service availability.
 * This is used by Spring Boot Actuator to monitor the health of external
 * dependencies.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceHealthIndicator implements HealthIndicator {

    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Override
    public Health health() {
        try {
            WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
            String response = webClient
                    .get()
                    .uri("/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            return Health.up()
                    .withDetail("service", "user-service")
                    .withDetail("url", userServiceUrl)
                    .withDetail("status", "Available")
                    .build();
        } catch (Exception e) {
            log.error("User Service health check failed", e);
            return Health.down()
                    .withDetail("service", "user-service")
                    .withDetail("url", userServiceUrl)
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Unavailable")
                    .build();
        }
    }
}