package kz.danke.kids.shop.exceptions;

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

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(exchange.getResponse());

        decorator.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        decorator.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        DataBufferFactory dataBufferFactory = decorator.bufferFactory();

        DataBuffer dataBuffer = dataBufferFactory.wrap("Something went wrong".getBytes());

        return decorator.writeWith(Flux.just(dataBuffer));
    }
}
