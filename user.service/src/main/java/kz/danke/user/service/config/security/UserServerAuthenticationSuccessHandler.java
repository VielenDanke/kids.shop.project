package kz.danke.user.service.config.security;

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

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        String token = userJwtService.generateToken(principal);

        webFilterExchange
                .getExchange()
                .getResponse()
                .getHeaders()
                .add(HttpHeaders.AUTHORIZATION, token);

        return webFilterExchange.getChain().filter(exchange);
    }
}
