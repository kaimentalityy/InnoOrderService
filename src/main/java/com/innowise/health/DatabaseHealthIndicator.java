package com.innowise.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Custom health indicator for database connectivity.
 * Checks if the database connection is valid and available.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "reachable")
                        .withDetail("validationTimeout", "1000ms")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "unreachable")
                        .withDetail("reason", "Connection validation failed")
                        .build();
            }
        } catch (SQLException e) {
            log.error("Database health check failed", e);
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "error")
                    .withDetail("error", e.getMessage())
                    .withException(e)
                    .build();
        }
    }
}
