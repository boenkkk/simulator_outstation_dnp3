package dev.boenkkk.simulator_outstation_dnp3.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public <T> String toJson(T object) {
        try {
            // Set the property naming strategy to use snake_case
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            return objectMapper.writeValueAsString(object);
        } catch (Throwable e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Throwable e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public <T> T fromJson(String json, TypeReference<T> tTypeReference) {
        try {
            return objectMapper.readValue(json, tTypeReference);
        } catch (Throwable e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public JsonNode toJsonNode(String message) {
        try {
            // Set the property naming strategy to use snake_case
            return objectMapper.readTree(message);
        } catch (Throwable e) {
            throw new RuntimeException("Error converting object to JSON Node", e);
        }
    }
}