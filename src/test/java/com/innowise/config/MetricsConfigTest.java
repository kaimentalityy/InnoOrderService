package com.innowise.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsConfigTest {

    private MetricsConfig metricsConfig;
    private MeterRegistry registry;

    @BeforeEach
    void setUp() {
        metricsConfig = new MetricsConfig();
        registry = new SimpleMeterRegistry();
    }

    @Test
    void metricsCommonTags_shouldAddTags() {
        var customizer = metricsConfig.metricsCommonTags();
        assertThat(customizer).isNotNull();
        customizer.customize(registry);
    }

    @Test
    void timedAspect_shouldBeCreated() {
        var aspect = metricsConfig.timedAspect(registry);
        assertThat(aspect).isNotNull();
    }

    @Test
    void ordersCreatedCounter_shouldBeCreated() {
        Counter counter = metricsConfig.ordersCreatedCounter(registry);
        assertThat(counter).isNotNull();
        counter.increment();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void ordersPendingCounter_shouldBeCreated() {
        Counter counter = metricsConfig.ordersPendingCounter(registry);
        assertThat(counter).isNotNull();
    }

    @Test
    void ordersCompletedCounter_shouldBeCreated() {
        Counter counter = metricsConfig.ordersCompletedCounter(registry);
        assertThat(counter).isNotNull();
    }

    @Test
    void ordersFailedCounter_shouldBeCreated() {
        Counter counter = metricsConfig.ordersFailedCounter(registry);
        assertThat(counter).isNotNull();
    }

    @Test
    void paymentEventsProcessedCounter_shouldBeCreated() {
        Counter counter = metricsConfig.paymentEventsProcessedCounter(registry);
        assertThat(counter).isNotNull();
    }

    @Test
    void orderProcessingTimer_shouldBeCreated() {
        Timer timer = metricsConfig.orderProcessingTimer(registry);
        assertThat(timer).isNotNull();
    }

    @Test
    void kafkaMessagesPublishedCounter_shouldBeCreated() {
        Counter counter = metricsConfig.kafkaMessagesPublishedCounter(registry);
        assertThat(counter).isNotNull();
    }

    @Test
    void userServiceCallsCounter_shouldBeCreated() {
        Counter counter = metricsConfig.userServiceCallsCounter(registry);
        assertThat(counter).isNotNull();
    }
}
