package kz.danke.user.service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class JsonObjectMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public <T> T deserializeInputStream(InputStream inputStream, Class<T> tClass) {
        T t = null;

        try {
            t = objectMapper.readValue(inputStream, tClass);
        } catch (Exception e) {
            log.error(e.toString(), "Json deserialization error");
        }
        return t;
    }
}
