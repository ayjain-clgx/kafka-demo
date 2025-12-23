package com.self.orders.consumer;

import com.self.core.dto.commands.ProcessPaymentCommand;
import com.self.core.dto.commands.ReserveProductCommand;
import com.self.core.dto.events.OrderCreatedEvent;
import com.self.core.dto.events.PaymentProcessedEvent;
import com.self.core.dto.events.ProductReservedEvent;
import com.self.core.types.OrderStatus;
import com.self.orders.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "${order.events.topic.name}",
        "${products.event.topic.name}",
        "${payments.events.topic.name}"
    }, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;

    @Value("${products.commands.topic.name}")
    private String productsCommandsTopicName;

    @Value("${payments.commands.topic.name}")
    private String paymentsCommandsTopicName;

    @KafkaHandler
    public void handleOrderEvents(@Payload OrderCreatedEvent event) {
        log.info("Received event: {}", event);
        var reserveProductCommand = new ReserveProductCommand(event.getOrderId(), event.getProductId(), event.getProductQuantity());
        kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);

        orderHistoryService.add(event.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleProductReservedEvents(@Payload ProductReservedEvent productReservedEvent) {
        log.info("Product reserved for order: {}", productReservedEvent.getOrderId());
        var paymentCommand = new ProcessPaymentCommand(productReservedEvent.getOrderId(),
                productReservedEvent.getProductId(),
                productReservedEvent.getProductQuantity(),
                productReservedEvent.getProductPrice()
                );

        kafkaTemplate.send(paymentsCommandsTopicName, paymentCommand);

    }

    @KafkaHandler
    public void handlePaymentEvents(@Payload PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Payment processed for order: {}", paymentProcessedEvent.getOrderId());


    }
}
