package kz.danke.user.service.config.security.converter;

import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.document.User;
import kz.danke.user.service.exception.TokenNotValidException;
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
    private final String accessTokenKey;
    private final String userClaimsKey;

    public UserAuthenticationPathFilterConverter(JwtService<String> jwtService,
                                                 JsonObjectMapper jsonObjectMapper,
                                                 String accessTokenKey,
                                                 String userClaimsKey) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
        this.accessTokenKey = accessTokenKey;
        this.userClaimsKey = userClaimsKey;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getHeaders())
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new TokenNotValidException("Token not found"))))
                .map(headers -> headers.getFirst(accessTokenKey))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new TokenNotValidException("Token not found"))))
                .flatMap(jwtService::validateToken)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new TokenNotValidException("Token is not valid"))))
                .map(jwtService::extractTokenClaims)
                .map(claims -> {
                    String userClaims = claims.get(userClaimsKey, String.class);
                    User user = jsonObjectMapper.deserializeJson(userClaims, User.class);
                    return new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            user.getAuthorities()
                                    .stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                    );
                });
    }
}
