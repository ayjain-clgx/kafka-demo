package com.self.products.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.core.dto.ProductCreateEvent;
import org.apache.kafka.common.serialization.Serializer;


public class CustomSerializer implements Serializer<ProductCreateEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, ProductCreateEvent productCreateEvent) {
        if (productCreateEvent == null) {
            return null;
        }
        try {
            return mapper.writeValueAsBytes(productCreateEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing ProductCreateEvent", e);
        }
    }
}
