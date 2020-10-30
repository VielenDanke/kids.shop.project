package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import kz.danke.edge.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
public class UserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtService<String> userJwtService;

    private ReactiveUserRepository reactiveUserRepository;

    public UserServerAuthenticationSuccessHandler(JwtService<String> userJwtService) {
        this.userJwtService = userJwtService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.setAuthentication(authentication))
                .then(Mono.just(authentication.getPrincipal()))
                .cast(UserDetailsImpl.class)
                .map(UserDetailsImpl::getUser)
                .flatMap(user -> {
                    String token = userJwtService.generateToken(user);

                    ServerWebExchange exchange = webFilterExchange.getExchange();

                    HttpHeaders headers = exchange.getResponse().getHeaders();

                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add("Roles", String.join(" ", user.getAuthorities()));

                    return webFilterExchange.getChain().filter(exchange);
                });
    }

    public void setReactiveUserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }
}
