package com.innowise.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Test security configuration that:
 * 1. Disables all security constraints so controller tests can focus on
 * business logic.
 * 2. Provides no-op mock beans for JWT/OAuth2 components so no network call is
 * made.
 * 3. Provides mock beans for WebClient dependencies so WebClientConfig can be
 * satisfied.
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * Permissive security filter chain — allows all requests without
     * authentication.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * No-op JwtDecoder — overrides the real Keycloak-backed one so no network call
     * is made during test context startup.
     */
    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        return token -> {
            throw new UnsupportedOperationException("JWT decoding not available in controller tests");
        };
    }

    /**
     * Mock ClientRegistrationRepository so WebClientConfig and KeycloakConfig can
     * satisfy
     * their dependencies without real OAuth2 server configuration.
     */
    @Bean
    @Primary
    public ClientRegistrationRepository testClientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    /**
     * Mock OAuth2AuthorizedClientService so WebClientConfig can be wired up without
     * connecting to Keycloak.
     */
    @Bean
    @Primary
    public OAuth2AuthorizedClientService testAuthorizedClientService() {
        return Mockito.mock(OAuth2AuthorizedClientService.class);
    }

    /**
     * Mock OAuth2AuthorizedClientManager — prevents WebClient from trying to fetch
     * tokens.
     */
    @Bean
    @Primary
    public OAuth2AuthorizedClientManager testAuthorizedClientManager() {
        return Mockito.mock(OAuth2AuthorizedClientManager.class);
    }

    /**
     * Simple WebClient with no OAuth2 filter — overrides the one in
     * WebClientConfig.
     */
    @Bean
    @Primary
    public WebClient testWebClient() {
        return WebClient.builder().build();
    }
}
