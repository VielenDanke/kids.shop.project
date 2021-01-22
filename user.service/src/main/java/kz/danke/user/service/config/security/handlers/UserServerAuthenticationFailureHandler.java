package kz.danke.user.service.config.security.handlers;

import kz.danke.user.service.exception.ResponseFailed;
import kz.danke.user.service.service.JsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class UserServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private JsonObjectMapper jsonObjectMapper;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.just(exception)
                .flatMap(ex -> {
                    ServerWebExchange exchange = webFilterExchange.getExchange();

                    ResponseFailed responseFailed = new ResponseFailed(
                            ex.getLocalizedMessage(),
                            ex.toString(),
                            exchange.getRequest().getPath().value()
                    );

                    ServerHttpResponse response = exchange.getResponse();

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    String responseFailedJson = jsonObjectMapper.serializeObject(responseFailed);

                    DataBuffer wrappedResponseFailedJson = dataBufferFactory.wrap(
                            responseFailedJson.getBytes(StandardCharsets.UTF_8)
                    );

                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    response.setRawStatusCode(401);

                    return response.writeWith(Flux.just(wrappedResponseFailedJson));
                });
    }

    public void setJsonObjectMapper(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }
}
