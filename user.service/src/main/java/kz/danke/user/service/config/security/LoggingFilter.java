package kz.danke.user.service.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Component
@Slf4j
public class LoggingFilter implements WebFilter {

    private ServerAuthenticationConverter authenticationConverter = new UserLoginFormAuthenticationConverter();
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return ServerWebExchangeMatchers.pathMatchers("/login")
                .matches(serverWebExchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .flatMap(matchResult -> this.authenticationConverter.convert(serverWebExchange))
                .switchIfEmpty(webFilterChain.filter(serverWebExchange).then(Mono.empty()))
                .flatMap(authentication -> this.authenticate(serverWebExchange, webFilterChain, authentication))
                .onErrorResume(AuthenticationException.class, (e) -> {
                    log.error(e.getLocalizedMessage());
                    return Mono.error(e);
                });
    }

    private Mono<Void> authenticate(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain, Authentication authentication) {
        return webFilterChain.filter(serverWebExchange);
    }

    public void setReactiveAuthenticationManager(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
    }
}
