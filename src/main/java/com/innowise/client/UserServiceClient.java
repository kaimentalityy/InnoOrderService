package com.innowise.client;

import com.innowise.model.dto.UserInfoDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl,
                             WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "userFallbackById")
    public UserInfoDto getUserById(Long userId, String jwtToken) {
        return webClient
                .get()
                .uri("/api/users/{id}", userId)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserInfoDto.class)
                .block();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "userFallbackByEmail")
    public UserInfoDto getUserByEmail(String email, String jwtToken) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/users/search")
                        .queryParam("email", email)
                        .build())
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserInfoDto.class)
                .block();
    }

    private UserInfoDto userFallbackById(Long id, String jwtToken, Throwable throwable) {
        log.warn("Fallback (getUserById) triggered: {}", throwable.getMessage());
        return createFallbackDto(id, "unknown@example.com");
    }

    private UserInfoDto userFallbackByEmail(String email, String jwtToken, Throwable throwable) {
        log.warn("Fallback (getUserByEmail) triggered: {}", throwable.getMessage());
        return createFallbackDto(-1L, email);
    }

    private UserInfoDto createFallbackDto(Long id, String email) {
        return new UserInfoDto(
                id,
                "Unknown",
                "User",
                email
        );
    }
}
