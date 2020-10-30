package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

@Slf4j
public class OAuthUserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private JwtService<String> jwtService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(authentication.toString());

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    securityContext.setAuthentication(authentication);

                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                    String authorizationToken = jwtService.generateToken(authentication);

                    response.getHeaders().add(HttpHeaders.AUTHORIZATION, authorizationToken);

                    return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                });
    }

    public void setJwtService(JwtService<String> jwtService) {
        this.jwtService = jwtService;
    }
}
