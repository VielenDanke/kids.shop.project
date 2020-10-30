package kz.danke.edge.service.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class UserServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    private final URI location;

    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    public UserServerAuthenticationFailureHandler(String location) {
        Assert.notNull(location, "Not empty location");
        this.location = URI.create(location);
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return this.redirectStrategy.sendRedirect(webFilterExchange.getExchange(), this.location);
    }

    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "Redirect strategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }
}
