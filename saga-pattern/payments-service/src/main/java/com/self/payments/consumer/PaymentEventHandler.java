package com.self.payments.consumer;

import com.self.core.dto.Payment;
import com.self.core.dto.commands.ProcessPaymentCommand;
import com.self.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${payments.commands.topic.name}"}, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    public void handlePaymentCommandEvent(@Payload ProcessPaymentCommand processPaymentCommand) {
        log.info("Received payment command event: {}", processPaymentCommand);

        var payment = new Payment(processPaymentCommand.getOrderId(),
                processPaymentCommand.getProductId(),
                processPaymentCommand.getProductPrice(),
                processPaymentCommand.getProductQuantity());

        paymentService.process(payment);
    }
}
