package com.self.emailnotification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.core.dto.ProductCreateEvent;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.logging.log4j.util.Strings;

import java.nio.ByteBuffer;
import java.util.Map;

public class CustomDeserializer implements Deserializer<ProductCreateEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public ProductCreateEvent deserialize(String s, byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        try {
            var object = objectMapper.readValue(bytes, ProductCreateEvent.class);
            if (Strings.isBlank(object.getName())) {
                throw new SerializationException("Name is empty");
            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing ProductCreateEvent", e);
        }
    }

    @Override
    public ProductCreateEvent deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public ProductCreateEvent deserialize(String topic, Headers headers, ByteBuffer data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
