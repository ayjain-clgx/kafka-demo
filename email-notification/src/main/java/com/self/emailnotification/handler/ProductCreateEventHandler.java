package com.self.emailnotification.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.self.core.dto.ProductCreateEvent;

@Component
@KafkaListener(topics = "product-created-events-topic")
@Slf4j
public class ProductCreateEventHandler {

    /**
     * Handle the ProductCreateEvent - Argument specifies which event to trigger in class when KafkaListener is annotated on class level.
     */
    @KafkaHandler
    public void handle(ProductCreateEvent productCreateEvent) {
        log.info("Received event {}", productCreateEvent);
    }
}
