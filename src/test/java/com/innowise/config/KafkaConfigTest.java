package com.innowise.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    private KafkaConfig createConfig() {
        KafkaConfig config = new KafkaConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(config, "trustedPackages", "com.innowise.*");
        ReflectionTestUtils.setField(config, "groupId", "test-group");
        ReflectionTestUtils.setField(config, "autoOffsetReset", "earliest");
        return config;
    }

    @Test
    void kafkaAdmin_shouldBeCreated() {
        KafkaConfig config = createConfig();
        KafkaAdmin admin = config.kafkaAdmin();
        assertThat(admin).isNotNull();
    }

    @Test
    void producerFactory_shouldBeCreated() {
        KafkaConfig config = createConfig();
        ProducerFactory<String, Object> factory = config.producerFactory();
        assertThat(factory).isNotNull();
    }

    @Test
    void kafkaTemplate_shouldBeCreated() {
        KafkaConfig config = createConfig();
        KafkaTemplate<String, Object> template = config.kafkaTemplate();
        assertThat(template).isNotNull();
    }

    @Test
    void consumerFactory_shouldBeCreated() {
        KafkaConfig config = createConfig();
        ConsumerFactory<String, Object> factory = config.consumerFactory();
        assertThat(factory).isNotNull();
    }

    @Test
    void errorHandler_shouldBeCreated() {
        KafkaConfig config = createConfig();
        KafkaTemplate<String, Object> template = config.kafkaTemplate();
        DefaultErrorHandler handler = config.errorHandler(template);
        assertThat(handler).isNotNull();
    }

    @Test
    void kafkaListenerContainerFactory_shouldBeCreated() {
        KafkaConfig config = createConfig();
        KafkaTemplate<String, Object> template = config.kafkaTemplate();
        DefaultErrorHandler handler = config.errorHandler(template);
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = config.kafkaListenerContainerFactory(handler);
        assertThat(factory).isNotNull();
    }
}
