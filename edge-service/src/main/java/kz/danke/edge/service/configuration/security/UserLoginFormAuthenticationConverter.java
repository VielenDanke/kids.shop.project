package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.dto.request.LoginRequest;
import kz.danke.edge.service.exception.EmptyLoginRequestBodyException;
import kz.danke.edge.service.exception.ParseLoginRequestException;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UserLoginFormAuthenticationConverter implements ServerAuthenticationConverter {

    private final JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange
                .getRequest()
                .getBody()
                .map(DataBuffer::asInputStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmptyLoginRequestBodyException("Body request login is empty"))))
                .map(inputStream -> jsonObjectMapper.deserializeInputStream(inputStream, LoginRequest.class))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ParseLoginRequestException("Cannot parse login request"))))
                .map(loginRequest ->
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
    }
}
