package com.self.products.consumer;

import com.self.core.dto.Product;
import com.self.core.dto.commands.ReserveProductCommand;
import com.self.core.dto.events.ProductReservedEvent;
import com.self.core.dto.events.ProductReservedFailedEvent;
import com.self.core.types.OrderStatus;
import com.self.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${products.commands.topic.name}"}, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Slf4j
public class ReserveProductCommandHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductService productService;

    @Value("${products.event.topic.name}")
    private String productsEventTopicName;

    @KafkaHandler
    public void handleOrderEvents(@Payload ReserveProductCommand event) {
        log.info("Received event: {}", event);
        try {
            var desriedProduct = new Product(event.getProductId(), event.getProductQuantity());
            var reservedProduct = productService.reserve(desriedProduct, event.getOrderId());

            var productReservedEvent = new ProductReservedEvent(
                    event.getProductId(),
                    event.getOrderId(),
                    reservedProduct.getPrice(),
                    event.getProductQuantity()
            );

            kafkaTemplate.send(productsEventTopicName, productReservedEvent);
        }
        catch (Exception e) {
            log.error("Error processing order event: {}", e.getLocalizedMessage());
            var productReservedFailedEvent = new ProductReservedFailedEvent(
                    event.getOrderId(),
                    event.getProductId(),
                    event.getProductQuantity()
            );
            kafkaTemplate.send(productsEventTopicName, productReservedFailedEvent);
        }
    }
}
