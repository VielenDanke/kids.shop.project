package kz.danke.edge.service.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class UserRedirectServerAuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    public UserRedirectServerAuthenticationSuccessHandler() {
        super();
    }

    public UserRedirectServerAuthenticationSuccessHandler(String location) {
        super(location);
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info(authentication.toString());
        log.info(webFilterExchange.toString());
        return super.onAuthenticationSuccess(webFilterExchange, authentication);
    }

    @Override
    public void setRequestCache(ServerRequestCache requestCache) {
        super.setRequestCache(requestCache);
    }

    @Override
    public void setLocation(URI location) {
        super.setLocation(location);
    }

    @Override
    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        super.setRedirectStrategy(redirectStrategy);
    }
}
