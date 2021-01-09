package kz.danke.user.service.config.security;

import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.document.User;
import kz.danke.user.service.exception.UserNotAuthorizedException;
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

    private String accessTokenKey;
    private String userClaimsKey;

    public UserAuthenticationPathFilterConverter(JwtService<String> jwtService,
                                                 JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getHeaders())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.empty())
                .flatMap(headers -> Mono.justOrEmpty(headers.getFirst(accessTokenKey)))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Token is empty"))))
                .flatMap(jwtService::validateToken)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Token is not valid"))))
                .map(jwtService::extractTokenClaims)
                .map(claims -> claims.get(userClaimsKey, String.class))
                .map(userClaims -> jsonObjectMapper.deserializeJson(userClaims, User.class))
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ));
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public void setUserClaimsKey(String userClaimsKey) {
        this.userClaimsKey = userClaimsKey;
    }
}
