package com.innowise.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class NewTopicConfigTest {

    @Test
    void orderEventsTopic_shouldCreateTopicWithCorrectName() {
        NewTopicConfig config = new NewTopicConfig();
        ReflectionTestUtils.setField(config, "ORDER_TOPIC", "order-events-test");

        NewTopic topic = config.orderEventsTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("order-events-test");
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }

    @Test
    void paymentEventsTopic_shouldCreateTopicWithCorrectName() {
        NewTopicConfig config = new NewTopicConfig();
        ReflectionTestUtils.setField(config, "PAYMENT_TOPIC", "payment-events-test");

        NewTopic topic = config.paymentEventsTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("payment-events-test");
        assertThat(topic.numPartitions()).isEqualTo(3);
    }

    @Test
    void paymentEventsDltTopic_shouldCreateDltTopic() {
        NewTopicConfig config = new NewTopicConfig();

        NewTopic topic = config.paymentEventsDltTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("payment-events.DLT");
    }
}
