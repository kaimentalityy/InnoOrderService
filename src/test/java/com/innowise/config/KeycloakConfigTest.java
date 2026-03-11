package com.innowise.config;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakConfigTest {

    @Test
    void keycloakClient_shouldBeCreated() {
        KeycloakConfig config = new KeycloakConfig();
        ReflectionTestUtils.setField(config, "serverUrl", "http://localhost:8080/");
        ReflectionTestUtils.setField(config, "realm", "test-realm");
        ReflectionTestUtils.setField(config, "clientId", "test-client-id");
        ReflectionTestUtils.setField(config, "clientSecret", "test-client-secret");

        Keycloak keycloak = config.keycloakClient();

        assertThat(keycloak).isNotNull();
    }
}
