package kz.danke.edge.service.configuration.security.handler;

import kz.danke.edge.service.exception.ResponseFailed;
import kz.danke.edge.service.service.JsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class OAuthUserServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private final JsonObjectMapper jsonObjectMapper;

    public OAuthUserServerAuthenticationFailureHandler(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.just(exception)
                .flatMap(ex -> {
                    log.warn("OAuth login failure", ex);

                    ResponseFailed responseFailed = new ResponseFailed(
                            ex.getLocalizedMessage(),
                            ex.toString(),
                            webFilterExchange.getExchange().getRequest().getPath().value()
                    );

                    String responseFailedJson = jsonObjectMapper.serializeObject(responseFailed);

                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                    response.setRawStatusCode(401);

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    DataBuffer wrappedResponseFailed = dataBufferFactory
                            .wrap(responseFailedJson.getBytes(StandardCharsets.UTF_8));

                    return response.writeWith(Flux.just(wrappedResponseFailed));
                });
    }
}
