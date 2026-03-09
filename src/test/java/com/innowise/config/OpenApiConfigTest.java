package com.innowise.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void orderServiceOpenAPI_returnsValidOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        ReflectionTestUtils.setField(config, "swaggerKeycloakAuthUrl",
                "http://localhost:8088/realms/test/protocol/openid-connect/auth");
        ReflectionTestUtils.setField(config, "swaggerKeycloakTokenUrl",
                "http://localhost:8088/realms/test/protocol/openid-connect/token");

        OpenAPI openAPI = config.orderServiceOpenAPI();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Innowise Order Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Innowise Team");
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080");
        assertThat(openAPI.getSecurity()).isNotEmpty();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("keycloak");
    }
}
