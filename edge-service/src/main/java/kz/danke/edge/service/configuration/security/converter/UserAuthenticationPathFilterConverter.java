package kz.danke.edge.service.configuration.security.converter;

import kz.danke.edge.service.configuration.security.jwt.JwtService;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

public class UserAuthenticationPathFilterConverter implements ServerAuthenticationConverter {

    private final JwtService<String> jwtService;
    private final JsonObjectMapper jsonObjectMapper;

    public UserAuthenticationPathFilterConverter(JwtService<String> jwtService,
                                                 JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");

        return Mono.just(exchange.getRequest().getHeaders())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.empty())
                .map(headers -> headers.getFirst("Authorization"))
                .filter(jwtService::validateToken)
                .map(jwtService::extractTokenClaims)
                .map(claims -> claims.get("user"))
                .map(userClaims -> jsonObjectMapper.deserializeJson((String) userClaims, User.class))
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ));
    }
}
