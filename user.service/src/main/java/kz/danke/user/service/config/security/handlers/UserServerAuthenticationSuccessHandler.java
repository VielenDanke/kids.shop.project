package kz.danke.user.service.config.security.handlers;

import kz.danke.user.service.config.security.UserDetailsImpl;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.dto.response.LoginResponse;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.JsonObjectMapper;
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
    private final String accessTokenKey;
    private final String rolesKey;

    private ReactiveUserRepository reactiveUserRepository;
    private JsonObjectMapper jsonObjectMapper;

    public UserServerAuthenticationSuccessHandler(JwtService<String> userJwtService,
                                                  String accessTokenKey,
                                                  String rolesKey) {
        this.userJwtService = userJwtService;
        this.accessTokenKey = accessTokenKey;
        this.rolesKey = rolesKey;
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

                    headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "accessToken, roles");
                    headers.set(accessTokenKey, token);
                    headers.set(rolesKey, String.join(" ", user.getAuthorities()));
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

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
