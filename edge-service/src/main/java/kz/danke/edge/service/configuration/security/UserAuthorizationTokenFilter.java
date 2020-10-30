package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.service.JsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserAuthorizationTokenFilter implements WebFilter {

    private final JwtService<String> jwtService;
    private final JsonObjectMapper jsonObjectMapper;

    @Autowired
    public UserAuthorizationTokenFilter(@Qualifier("userJwtService") JwtService<String> jwtService,
                                        JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String authorizationHeaderName = "Authorization";
        final String userClaimsKey = "user";

        List<String> headers = exchange.getRequest().getHeaders().get(authorizationHeaderName);

        Mono<SecurityContext> securityContext = ReactiveSecurityContextHolder.getContext();

        if (headers != null) {
            return Flux.fromIterable(headers)
                    .filter(token -> !StringUtils.isEmpty(token))
                    .singleOrEmpty()
                    .filter(jwtService::validateToken)
                    .map(jwtService::extractTokenClaims)
                    .map(claims -> claims.get(userClaimsKey))
                    .map(userClaims -> jsonObjectMapper.deserializeJson((String) userClaims, User.class))
                    .zipWith(securityContext)
                    .map(tuple -> {
                        SecurityContext context = tuple.getT2();
                        User user = tuple.getT1();

                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                null,
                                user.getAuthorities()
                                        .stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList())
                        );
                        context.setAuthentication(token);

                        return user;
                    })
                    .flatMap(user -> chain.filter(exchange))
                    .switchIfEmpty(
                            Mono.just(exchange)
                                    .doOnNext(exc -> exc.getResponse().setRawStatusCode(401))
                                    .flatMap(chain::filter)
                    )
                    .onErrorResume(ex -> {
                        exchange.getResponse().setRawStatusCode(401);
                        return chain.filter(exchange);
                    });
        }
        return chain.filter(exchange);
    }
}
