package kz.danke.edge.service.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

@Slf4j
public class UserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(authentication.toString());
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    securityContext.setAuthentication(authentication);
                    return webFilterExchange.getExchange().getResponse().setComplete();
                });
    }
}
