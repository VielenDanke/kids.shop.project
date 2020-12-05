package kz.danke.kids.shop.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(exchange.getResponse());
        ResponseFailed responseFailed = ResponseFailed.builder()
                .description(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "Something went wrong")
                .type(ex.toString())
                .build();
        DataBufferFactory dataBufferFactory = decorator.bufferFactory();

        decorator.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        decorator.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            String responseFailedJson = objectMapper.writeValueAsString(responseFailed);
            DataBuffer dataBuffer = dataBufferFactory.wrap(responseFailedJson.getBytes());
            return decorator.writeWith(Flux.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to write response exception", e);
        }
        return decorator.setComplete();
    }
}
