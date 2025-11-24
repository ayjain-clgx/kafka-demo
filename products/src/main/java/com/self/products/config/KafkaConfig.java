package com.self.products.config;

import com.self.core.dto.ProductCreateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static com.self.products.constants.KafkaConstants.PRODUCT_CREATE_TOPIC;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private Integer deliveryTimeoutMs;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private Integer requestTimeoutMs;

    @Value("${spring.kafka.producer.properties.lingered.ms}")
    private Integer lingerMs;


    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(PRODUCT_CREATE_TOPIC)
                .partitions(3)
                .replicas(1) // If you have multiple brokers, increase the number of replicas
//                .configs(Map.of("min.insync.replicas", "1")) // Property which states that at least 1 replica must acknowledge the write operation.
                .build();
    }

    Map<String, Object> producerConfigs() {
        return Map.of(
                ProducerConfig.ACKS_CONFIG, acks,
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class,
                ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs,
                ProducerConfig.LINGER_MS_CONFIG, lingerMs,
                ProducerConfig.RETRIES_CONFIG, retries,
                ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs
        );
    }

    @Bean
    ProducerFactory<String, ProductCreateEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, ProductCreateEvent> kafkaTemplate(ProducerFactory<String, ProductCreateEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


}
