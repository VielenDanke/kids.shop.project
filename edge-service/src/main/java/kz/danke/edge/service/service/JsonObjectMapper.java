package kz.danke.edge.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JsonObjectMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> String serializeObject(T t) {
        String json = null;

        try {
            json = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error(e.toString(), "Json serialization error");
        }
        return json;
    }

    public <T> T deserializeJson(String json, Class<T> tClass) {
        T t = null;

        try {
            t = objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error(e.toString(), "Json deserialization error");
        }
        return t;
    }
}
