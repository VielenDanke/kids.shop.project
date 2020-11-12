package kz.danke.user.service.config.security;

import io.jsonwebtoken.Claims;
import kz.danke.user.service.document.User;
import kz.danke.user.service.service.JsonObjectMapper;
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
        boolean b = jwtService.validateToken(authorization);
        Claims claims1 = jwtService.extractTokenClaims(authorization);
        String user1 = claims1.get("user", String.class);
        User user2 = jsonObjectMapper.deserializeJson(user1, User.class);

        return Mono.just(exchange.getRequest().getHeaders())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.empty())
                .map(headers -> headers.getFirst("Authorization"))
                .filter(jwtService::validateToken)
                .map(jwtService::extractTokenClaims)
                .map(claims -> claims.get("user", String.class))
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
