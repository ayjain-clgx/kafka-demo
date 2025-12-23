package com.self.payments.consumer;

import com.self.core.dto.Payment;
import com.self.core.dto.commands.ProcessPaymentCommand;
import com.self.core.dto.events.PaymentFailedEvent;
import com.self.core.dto.events.PaymentProcessedEvent;
import com.self.core.exceptions.CreditCardProcessorUnavailableException;
import com.self.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${payments.events.topic.name}")
    private String paymentsEventsTopicName;

    @KafkaHandler
    public void handlePaymentCommandEvent(@Payload ProcessPaymentCommand processPaymentCommand) {
        log.info("Received payment command event: {}", processPaymentCommand);

        try {
            var payment = new Payment(processPaymentCommand.getOrderId(),
                    processPaymentCommand.getProductId(),
                    processPaymentCommand.getProductPrice(),
                    processPaymentCommand.getProductQuantity());

            var paymentSuccessful = paymentService.process(payment);

            var paymentProcessedEvent = new PaymentProcessedEvent(paymentSuccessful.getOrderId(), paymentSuccessful.getId());

            kafkaTemplate.send(paymentsEventsTopicName, paymentProcessedEvent);
        }
        catch (CreditCardProcessorUnavailableException e) {
            log.error("Credit card processor unavailable: {}", e.getLocalizedMessage());
            var paymentFailedEvent = new PaymentFailedEvent(processPaymentCommand.getOrderId(), processPaymentCommand.getProductId(), processPaymentCommand.getProductQuantity());
            kafkaTemplate.send(paymentsEventsTopicName, paymentFailedEvent);
        }
    }
}
