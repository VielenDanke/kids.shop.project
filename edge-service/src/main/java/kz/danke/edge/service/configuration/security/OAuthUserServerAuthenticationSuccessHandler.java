package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.User;
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
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

@Slf4j
public class OAuthUserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private JwtService<String> jwtService;
    private ReactiveUserRepository reactiveUserRepository;
    private JsonObjectMapper jsonObjectMapper;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(authentication.toString());

        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.setAuthentication(authentication))
                .map(securityContext -> authentication.getPrincipal())
                .cast(DefaultOidcUser.class)
                .flatMap(defaultOAuth2User -> {
                    final String emailAttribute = "email";
                    return reactiveUserRepository.findByUsername(
                            defaultOAuth2User.getAttribute(emailAttribute)
                    );
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
                                    return reactiveUserRepository.save(
                                            User.builder()
                                                    .id(UUID.randomUUID().toString())
                                                    .username(principal.getAttribute("email"))
                                                    .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                                                    .build()
                                            );
                                }
                        )
                )
                .flatMap(user -> {
                    String token = jwtService.generateToken(user);

                    ServerWebExchange exchange = webFilterExchange.getExchange();

                    ServerHttpResponse response = exchange.getResponse();

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    LoginResponse loginResponse = new LoginResponse(user.getId(), user.getUsername());

                    String loginResponseJson = jsonObjectMapper.serializeObject(loginResponse);

                    DataBuffer wrappedLoginResponseJson = dataBufferFactory
                            .wrap(loginResponseJson.getBytes(StandardCharsets.UTF_8));

                    HttpHeaders headers = response.getHeaders();

                    String roles = String.join(" ", user.getAuthorities());

                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add("Roles", roles);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    response.setStatusCode(HttpStatus.OK);

                    return response.writeWith(Flux.just(wrappedLoginResponseJson));
                })
                .onErrorResume(Exception.class, ex -> {
                    webFilterExchange.getExchange().getResponse().setRawStatusCode(401);

                    webFilterExchange.getExchange().getResponse().setComplete();

                    return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                });

    }

    public void setJwtService(JwtService<String> jwtService) {
        this.jwtService = jwtService;
    }

    public void setReactiveUserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    public void setJsonObjectMapper(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }
}
