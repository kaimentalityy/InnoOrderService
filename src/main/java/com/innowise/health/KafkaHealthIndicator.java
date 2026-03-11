package com.innowise.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Custom health indicator to check Kafka cluster availability.
 * This is used by Spring Boot Actuator to monitor Kafka connectivity.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    @Override
    public Health health() {
        Map<String, Object> config = kafkaAdmin.getConfigurationProperties();
        if (config == null) {
            return Health.down()
                    .withDetail("error", "Kafka configuration properties are null")
                    .withDetail("status", "Disconnected")
                    .build();
        }

        try (AdminClient adminClient = AdminClient.create(config)) {
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            String clusterId = clusterResult.clusterId().get(5, TimeUnit.SECONDS);
            int nodeCount = clusterResult.nodes().get(5, TimeUnit.SECONDS).size();

            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodeCount", nodeCount)
                    .withDetail("bootstrap.servers", config.getOrDefault("bootstrap.servers", "unknown"))
                    .withDetail("status", "Connected")
                    .build();
        } catch (Exception e) {
            log.error("Kafka health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage() != null ? e.getMessage() : e.getClass().getName())
                    .withDetail("status", "Disconnected")
                    .build();
        }
    }
}
