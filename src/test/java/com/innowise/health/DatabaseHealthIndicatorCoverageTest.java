package com.innowise.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Additional tests for DatabaseHealthIndicator to cover the "connection not
 * valid" branch.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorCoverageTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseHealthIndicator healthIndicator;

    @Test
    void health_connectionNotValid_shouldReturnDown() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1000)).thenReturn(false);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().get("database")).isEqualTo("PostgreSQL");
        assertThat(health.getDetails().get("status")).isEqualTo("unreachable");
        assertThat(health.getDetails().get("reason")).isEqualTo("Connection validation failed");

        verify(connection).close();
    }
}
