package com.innowise.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for KafkaHealthIndicator.
 */
@ExtendWith(MockitoExtension.class)
class KafkaHealthIndicatorTest {

    @Mock
    private KafkaAdmin kafkaAdmin;

    @InjectMocks
    private KafkaHealthIndicator healthIndicator;

    @Test
    void shouldReturnHealthStatus() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        when(kafkaAdmin.getConfigurationProperties()).thenReturn(config);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("bootstrap.servers", "localhost:9092");
    }
}