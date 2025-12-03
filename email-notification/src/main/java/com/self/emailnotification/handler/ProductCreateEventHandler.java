package com.self.emailnotification.handler;

import com.self.emailnotification.entity.ProductEventEntity;
import com.self.emailnotification.exception.NonRetryableException;
import com.self.emailnotification.exception.RetryableException;
import com.self.emailnotification.repository.ProductEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.self.core.dto.ProductCreateEvent;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic", containerFactory = "kafkaListenerContainerFactory")
@Slf4j
@RequiredArgsConstructor
public class ProductCreateEventHandler {

    private final RestTemplate restTemplate;
    private final ProductEventRepository productEventRepository;

    /**
     * Handle the ProductCreateEvent - Argument specifies which event to trigger in class when KafkaListener is annotated on class level.
     */
    @KafkaHandler
    @Transactional
    public void handle(@Payload ProductCreateEvent productCreateEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Received event {}", productCreateEvent);
//        try {
//            var response = restTemplate.exchange("http://localhost:8081/products", HttpMethod.GET, null, String.class);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                log.info("Received response");
//            }
//        }
//        catch (ResourceAccessException e) {
//            log.error("Resource access exception: {}", e.getMessage());
//            throw new RetryableException(e.getMessage());
//        }
//        catch (Exception e) {
//            log.error("Unexpected exception {}", e.getMessage());
//            throw new NonRetryableException(e.getMessage());
//        }

        var existingRecords = productEventRepository.findByMessageId(messageId);
        if (!existingRecords.isEmpty()) {
            log.info("Event already exists in product {}", existingRecords);
            return;
        }
        try {
            productEventRepository.save(new ProductEventEntity(messageId, messageKey));
        }
        catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage());
            throw new NonRetryableException(e.getMessage());
        }

    }
}
