package com.innowise.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WebClientConfigTest {

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Test
    void authorizedClientManager_shouldBeCreated() {
        WebClientConfig config = new WebClientConfig();

        ClientRegistration registration = ClientRegistration
                .withRegistrationId("internal-service-client")
                .clientId("test-client")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost/token")
                .build();
        ClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(registration);

        OAuth2AuthorizedClientManager manager = config.authorizedClientManager(repository, authorizedClientService);

        assertThat(manager).isNotNull();
    }

    @Test
    void webClient_shouldBeCreated() {
        WebClientConfig config = new WebClientConfig();

        ClientRegistration registration = ClientRegistration
                .withRegistrationId("internal-service-client")
                .clientId("test-client")
                .clientSecret("test-secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost/token")
                .build();
        ClientRegistrationRepository repository = new InMemoryClientRegistrationRepository(registration);

        OAuth2AuthorizedClientManager manager = config.authorizedClientManager(repository, authorizedClientService);
        WebClient webClient = config.webClient(manager);

        assertThat(webClient).isNotNull();
    }
}
