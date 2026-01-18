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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseHealthIndicator.
 */
@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseHealthIndicator healthIndicator;

    @Test
    void health_WhenDatabaseIsHealthy_ShouldReturnUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1000)).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
        assertEquals("reachable", health.getDetails().get("status"));
        assertEquals("1000ms", health.getDetails().get("validationTimeout"));

        verify(connection).close();
    }

    @Test
    void health_WhenDatabaseIsDown_ShouldReturnDown() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
        assertEquals("error", health.getDetails().get("status"));
        assertTrue(health.getDetails().containsKey("error"));
    }
}
