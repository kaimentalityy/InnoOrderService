package com.innowise.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewTopicConfig {

    private static final int NUM_PARTITIONS = 3;
    private static final short REPLICATION_FACTOR = 1;

    @Value("${spring.kafka.topic.order-events}")
    private String ORDER_TOPIC;

    @Value("${spring.kafka.topic.payment-events}")
    private String PAYMENT_TOPIC;

    @Bean
    public NewTopic orderEventsTopic() {
        return new NewTopic(ORDER_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return new NewTopic(PAYMENT_TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR);
    }
    
    @Bean
    public NewTopic paymentEventsDltTopic() {
        return new NewTopic("payment-events.DLT", NUM_PARTITIONS, REPLICATION_FACTOR);
    }
}
