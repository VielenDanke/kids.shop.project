package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class UserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtService<String> userJwtService;

    public UserServerAuthenticationSuccessHandler(JwtService<String> userJwtService) {
        this.userJwtService = userJwtService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        String token = userJwtService.generateToken(authentication);

        webFilterExchange
                .getExchange()
                .getResponse()
                .getHeaders()
                .add(HttpHeaders.AUTHORIZATION, token);

        return webFilterExchange.getChain().filter(exchange);
    }
}
