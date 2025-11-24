package com.self.kafkademo.service;

import com.self.kafkademo.dto.ProductCreateEvent;
import com.self.kafkademo.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.self.kafkademo.constants.KafkaConstants.PRODUCT_CREATE_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final KafkaTemplate<String, ProductCreateEvent> kafkaTemplate;

    public Product addProduct(Product product) {
        var id = UUID.randomUUID().toString();
        var productEvent = new ProductCreateEvent(id, product.getName());
        var responseFuture = kafkaTemplate.send(PRODUCT_CREATE_TOPIC, productEvent);
        responseFuture.whenComplete((resp, e) -> {
            if (e != null) {
                log.error("Failed to send message: {}", e.getMessage());
            }
            else {
                log.info("Message sent successfully: {}", resp.getRecordMetadata());
                product.setId(id);
            }
        });
//        responseFuture.join(); // Make above call synchronous.
        return product;
    }
}
