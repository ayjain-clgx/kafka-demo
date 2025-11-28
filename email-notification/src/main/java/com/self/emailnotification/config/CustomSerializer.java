package com.self.emailnotification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.core.dto.ProductCreateEvent;
import org.apache.kafka.common.serialization.Serializer;


public class CustomSerializer implements Serializer<Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Object object) {
        if (object == null) {
            return null;
        }
        try {
            return mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing ProductCreateEvent", e);
        }
    }
}
