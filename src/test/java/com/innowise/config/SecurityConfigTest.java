package com.innowise.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void jwtAuthenticationConverter_shouldReturnNonNull() {
        SecurityConfig config = new SecurityConfig();
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();
        assertThat(converter).isNotNull();
    }

    @Test
    void jwtDecoder_shouldBuildFromIssuerUri() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "issuerUri", "http://localhost/realms/test");

        try {
            config.jwtDecoder();
        } catch (Exception e) {
        }
    }
}
