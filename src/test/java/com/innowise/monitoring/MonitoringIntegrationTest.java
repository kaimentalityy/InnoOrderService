package com.innowise.monitoring;

import com.innowise.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for monitoring and observability features.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
                "org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration," +
                "io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration," +
                "io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakersHealthIndicatorAutoConfiguration,"
                +
                "io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration," +
                "io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration," +
                "io.github.resilience4j.springboot3.bulkhead.autoconfigure.BulkheadAutoConfiguration," +
                "io.github.resilience4j.springboot3.timelimiter.autoconfigure.TimeLimiterAutoConfiguration," +
                "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
                "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,"
                +
                "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration"
})
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class MonitoringIntegrationTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        public io.micrometer.core.instrument.MeterRegistry meterRegistry() {
            return new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
        }

        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        public io.opentelemetry.api.trace.Tracer tracer() {
            return io.opentelemetry.api.OpenTelemetry.noop().getTracer("noop");
        }

        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        public io.opentelemetry.api.OpenTelemetry openTelemetry() {
            return io.opentelemetry.api.OpenTelemetry.noop();
        }
    }

    @MockBean
    private com.innowise.service.OrderService orderService;

    @MockBean
    private com.innowise.service.ItemService itemService;

    @MockBean
    private com.innowise.service.OrderItemService orderItemService;

    @MockBean
    private com.innowise.health.UserServiceHealthIndicator userServiceHealthIndicator;

    @MockBean
    private com.innowise.health.DatabaseHealthIndicator databaseHealthIndicator;

    @MockBean
    private com.innowise.health.KafkaHealthIndicator kafkaHealthIndicator;

    @MockBean
    private com.innowise.service.kafka.OrderEventProducer orderEventProducer;

    @MockBean
    private com.innowise.client.UserServiceClient userServiceClient;

    @MockBean
    private com.innowise.dao.repository.OrderRepository orderRepository;

    @MockBean
    private com.innowise.dao.repository.ItemRepository itemRepository;

    @MockBean
    private com.innowise.dao.repository.OrderItemRepository orderItemRepository;

    @MockBean
    private org.keycloak.admin.client.Keycloak keycloakClient;

    @MockBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldExposeHealthEndpoint() {
        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }

    @Test
    void shouldExposePrometheusMetricsEndpoint() {
        String url = "http://localhost:" + port + "/actuator/prometheus";
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}