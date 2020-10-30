package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Slf4j
public class OAuthUserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private JwtService<String> jwtService;
    private ReactiveUserRepository reactiveUserRepository;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(authentication.toString());

        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.setAuthentication(authentication))
                .map(securityContext -> authentication.getPrincipal())
                .cast(DefaultOAuth2User.class)
                .flatMap(defaultOAuth2User -> reactiveUserRepository.findByUsername(
                        defaultOAuth2User.getAttribute("email")
                ))
                .switchIfEmpty(
                        reactiveUserRepository.save(
                                User.builder()
                                        .id(UUID.randomUUID().toString())
                                        .username(((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("email"))
                                        .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                                        .build()
                        )
                )
                .flatMap(user -> {
                    String token = jwtService.generateToken(user);

                    HttpHeaders headers = webFilterExchange.getExchange().getResponse().getHeaders();

                    String roles = String.join(" ", user.getAuthorities());

                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add("Roles", roles);

                    return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                })
                .onErrorResume(ex -> {
                    webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

                    return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                });

    }

    public void setJwtService(JwtService<String> jwtService) {
        this.jwtService = jwtService;
    }

    public void setReactiveUserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }
}
