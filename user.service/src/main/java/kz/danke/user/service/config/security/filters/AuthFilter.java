package kz.danke.user.service.config.security.filters;

import io.jsonwebtoken.JwtException;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AuthFilter implements WebFilter {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository.getInstance();
    private ServerAuthenticationConverter authenticationConverter = new ServerFormLoginAuthenticationConverter();
    private ServerWebExchangeMatcher serverWebExchangeMatcher = ServerWebExchangeMatchers.pathMatchers("/**");
    private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();
    private ServerAuthenticationFailureHandler authenticationFailureHandler = new RedirectServerAuthenticationFailureHandler("/login?error");

    public AuthFilter(ReactiveAuthenticationManager reactiveAuthenticationManager) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        return this.serverWebExchangeMatcher.matches(serverWebExchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .flatMap(matchResult -> this.authenticationConverter.convert(serverWebExchange))
                .switchIfEmpty(webFilterChain.filter(serverWebExchange).then(Mono.empty()))
                .flatMap(authentication -> this.authenticate(serverWebExchange, webFilterChain, authentication))
                .onErrorResume(AuthenticationException.class, (e) -> this.authenticationFailureHandler.onAuthenticationFailure(
                        new WebFilterExchange(serverWebExchange, webFilterChain), e
                ))
                .onErrorResume(JwtException.class, (e) -> this.authenticationFailureHandler.onAuthenticationFailure(
                        new WebFilterExchange(serverWebExchange, webFilterChain), new AuthenticationException(e.getLocalizedMessage(), e) {}
                ))
                .onErrorResume(UserNotAuthorizedException.class, (e) -> this.authenticationFailureHandler.onAuthenticationFailure(
                        new WebFilterExchange(serverWebExchange, webFilterChain), new AuthenticationException(e.getLocalizedMessage(), e) {}
                ));
    }

    private Mono<Void> authenticate(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain, Authentication authentication) {
        return this.reactiveAuthenticationManager.authenticate(authentication)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalStateException("No provider found for " + authentication.getClass()))))
                .flatMap(auth -> onAuthenticationSuccess(auth, new WebFilterExchange(serverWebExchange, webFilterChain)));
    }

    private Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        return this.securityContextRepository.save(exchange, securityContext)
                .then(this.authenticationSuccessHandler
                        .onAuthenticationSuccess(webFilterExchange, authentication))
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    public void setServerWebExchangeMatherWithPathMatchers(String[] getMatchers, String[] postMatchers, String[] deleteMatchers) {
        List<ServerWebExchangeMatcher> matchers = new ArrayList<>();
        if (getMatchers.length > 0) {
            ServerWebExchangeMatcher getMatcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, getMatchers);
            matchers.add(getMatcher);
        }
        if (postMatchers.length > 0) {
            ServerWebExchangeMatcher postMatcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, postMatchers);
            matchers.add(postMatcher);
        }
        if (deleteMatchers.length > 0) {
            ServerWebExchangeMatcher deleteMatcher = ServerWebExchangeMatchers.pathMatchers(HttpMethod.DELETE, deleteMatchers);
            matchers.add(deleteMatcher);
        }
        this.serverWebExchangeMatcher = ServerWebExchangeMatchers
                .matchers(matchers.toArray(ServerWebExchangeMatcher[]::new));
    }

    public void setAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
    }

    public void setServerWebExchangeMatcher(ServerWebExchangeMatcher serverWebExchangeMatcher) {
        this.serverWebExchangeMatcher = serverWebExchangeMatcher;
    }

    public void setSecurityContextRepository(ServerSecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    public void setAuthenticationSuccessHandler(ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    public void setAuthenticationFailureHandler(ServerAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }
}
