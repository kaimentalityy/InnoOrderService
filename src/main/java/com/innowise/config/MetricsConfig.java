package com.innowise.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for custom business metrics using Micrometer.
 * These metrics will be exposed via Prometheus and visualized in Grafana.
 */
@Configuration
@EnableAspectJAutoProxy
public class MetricsConfig {

    /**
     * Customizes the MeterRegistry to add common tags to all metrics.
     * These tags help identify metrics in Prometheus/Grafana.
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags(
                        "application", "inno-order-service",
                        "service", "order-service");
    }

    /**
     * Enables @Timed annotation support for method execution timing.
     * This allows precise timing of specific methods.
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Counter for tracking total orders created
     */
    @Bean
    public Counter ordersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("orders.created.total")
                .description("Total number of orders created")
                .tag("service", "order-service")
                .register(registry);
    }

    /**
     * Counter for tracking orders by status
     */
    @Bean
    public Counter ordersPendingCounter(MeterRegistry registry) {
        return Counter.builder("orders.status.pending")
                .description("Number of pending orders")
                .tag("service", "order-service")
                .tag("status", "pending")
                .register(registry);
    }

    @Bean
    public Counter ordersCompletedCounter(MeterRegistry registry) {
        return Counter.builder("orders.status.completed")
                .description("Number of completed orders")
                .tag("service", "order-service")
                .tag("status", "completed")
                .register(registry);
    }

    @Bean
    public Counter ordersFailedCounter(MeterRegistry registry) {
        return Counter.builder("orders.status.failed")
                .description("Number of failed orders")
                .tag("service", "order-service")
                .tag("status", "failed")
                .register(registry);
    }

    /**
     * Counter for tracking payment events
     */
    @Bean
    public Counter paymentEventsProcessedCounter(MeterRegistry registry) {
        return Counter.builder("payment.events.processed")
                .description("Total number of payment events processed")
                .tag("service", "order-service")
                .register(registry);
    }

    /**
     * Timer for tracking order processing duration
     */
    @Bean
    public Timer orderProcessingTimer(MeterRegistry registry) {
        return Timer.builder("order.processing.duration")
                .description("Time taken to process an order")
                .tag("service", "order-service")
                .register(registry);
    }

    /**
     * Counter for tracking Kafka message publishing
     */
    @Bean
    public Counter kafkaMessagesPublishedCounter(MeterRegistry registry) {
        return Counter.builder("kafka.messages.published")
                .description("Total number of Kafka messages published")
                .tag("service", "order-service")
                .register(registry);
    }

    /**
     * Counter for tracking user service calls
     */
    @Bean
    public Counter userServiceCallsCounter(MeterRegistry registry) {
        return Counter.builder("user.service.calls.total")
                .description("Total number of calls to User Service")
                .tag("service", "order-service")
                .register(registry);
    }
}
