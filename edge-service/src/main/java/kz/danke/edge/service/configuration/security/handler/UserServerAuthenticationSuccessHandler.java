package kz.danke.edge.service.configuration.security.handler;

import kz.danke.edge.service.configuration.security.UserDetailsImpl;
import kz.danke.edge.service.configuration.security.jwt.JwtService;
import kz.danke.edge.service.dto.response.LoginResponse;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import kz.danke.edge.service.service.JsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class UserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtService<String> userJwtService;

    private ReactiveUserRepository reactiveUserRepository;
    private JsonObjectMapper jsonObjectMapper;

    public UserServerAuthenticationSuccessHandler(JwtService<String> userJwtService) {
        this.userJwtService = userJwtService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.setAuthentication(authentication))
                .then(Mono.just(authentication.getPrincipal()))
                .cast(UserDetailsImpl.class)
                .map(UserDetailsImpl::getUser)
                .flatMap(user -> {
                    String token = userJwtService.generateToken(user);

                    ServerWebExchange exchange = webFilterExchange.getExchange();

                    ServerHttpResponse response = exchange.getResponse();

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername());

                    String loginResponseJson = jsonObjectMapper.serializeObject(loginResponse);

                    DataBuffer wrappedLoginResponseJson = dataBufferFactory
                            .wrap(loginResponseJson.getBytes(StandardCharsets.UTF_8));

                    response.setStatusCode(HttpStatus.OK);

                    HttpHeaders headers = response.getHeaders();

                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add("Roles", String.join(" ", user.getAuthorities()));
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    return response.writeWith(Flux.just(wrappedLoginResponseJson));
                });
    }

    public void setReactiveUserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    public void setJsonObjectMapper(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }
}