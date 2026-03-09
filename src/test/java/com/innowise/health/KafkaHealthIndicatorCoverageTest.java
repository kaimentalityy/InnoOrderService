package com.innowise.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.kafka.core.KafkaAdmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Additional tests for KafkaHealthIndicator to cover null config branch.
 */
@ExtendWith(MockitoExtension.class)
class KafkaHealthIndicatorCoverageTest {

    @Mock
    private KafkaAdmin kafkaAdmin;

    @InjectMocks
    private KafkaHealthIndicator healthIndicator;

    @Test
    void health_nullConfig_shouldReturnDown() {
        when(kafkaAdmin.getConfigurationProperties()).thenReturn(null);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("error", "Kafka configuration properties are null");
        assertThat(health.getDetails()).containsEntry("status", "Disconnected");
    }
}
