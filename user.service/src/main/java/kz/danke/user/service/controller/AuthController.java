package kz.danke.user.service.controller;

import kz.danke.user.service.config.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(ServerWebExchange serverWebExchange) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserDetailsImpl.class)
                .doOnSuccess(userDetails -> {
                    serverWebExchange.getResponse().getHeaders().add(HttpHeaders.AUTHORIZATION, "token");
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/")
    public Mono<OAuth2User> index(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                                  @AuthenticationPrincipal OAuth2User oauth2User) {
        log.info(authorizedClient.getPrincipalName());
        log.info(oauth2User.getName());
        return Mono.just(oauth2User);
    }
}
