package com.self.kafkademo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

import static com.self.kafkademo.constants.KafkaConstants.PRODUCT_CREATE_TOPIC;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(PRODUCT_CREATE_TOPIC)
                .partitions(3)
                .replicas(1) // If you have multiple brokers, increase the number of replicas
//                .configs(Map.of("min.insync.replicas", "1")) // Property which states that at least 1 replica must acknowledge the write operation.
                .build();
    }


}
