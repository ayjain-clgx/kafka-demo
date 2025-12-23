package com.self.orders.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${order.events.topic.name}")
    private String orderEventsTopicName;

    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @Value("${payments.commands.topic.name}")
    private String paymentsCommandsTopicName;

    @Bean
    NewTopic orderEventsTopic() {
        return TopicBuilder.name(orderEventsTopicName)
                .replicas(3)
                .partitions(3)
                .build();
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic productsCommandTopic() {
        return TopicBuilder.name(productsCommandsTopicName)
                .replicas(3)
                .partitions(3)
                .build();
    }

    @Bean
    NewTopic paymentsCommandTopic() {
        return TopicBuilder.name(paymentsCommandsTopicName)
                .replicas(3)
                .partitions(3)
                .build();
    }

}
