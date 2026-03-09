package com.innowise.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides API documentation accessible at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {
        /**
         * Browser-facing Keycloak token URL. Must use localhost (not the internal
         * Docker
         * hostname) because Swagger UI runs in the browser, not inside Docker.
         * Injected via SWAGGER_KEYCLOAK_TOKEN_URL env var in Docker Compose.
         */
        @Value("${swagger.keycloak.auth-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/auth}")
        private String swaggerKeycloakAuthUrl;

        @Value("${swagger.keycloak.token-url:http://localhost:8088/realms/innowise-realm/protocol/openid-connect/token}")
        private String swaggerKeycloakTokenUrl;

        @Bean
        public OpenAPI orderServiceOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Innowise Order Service API")
                                                .description("REST API for managing orders, items, and order items in the Innowise Order Service")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Innowise Team")
                                                                .email("support@innowise.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .addServersItem(new Server()
                                                .url("http://localhost:8080")
                                                .description("API Gateway"))
                                .addSecurityItem(new SecurityRequirement().addList("keycloak"))
                                .components(new Components()
                                                .addSecuritySchemes("keycloak",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.OAUTH2)
                                                                                .description("Authenticate via Keycloak (Authorization Code flow)")
                                                                                .flows(new OAuthFlows()
                                                                                                .authorizationCode(
                                                                                                                new OAuthFlow()
                                                                                                                                .authorizationUrl(
                                                                                                                                                swaggerKeycloakAuthUrl)
                                                                                                                                .tokenUrl(swaggerKeycloakTokenUrl)))));
        }
}
