package kz.danke.user.service.config.security.jwt;

import io.jsonwebtoken.Claims;
import kz.danke.user.service.document.User;
import reactor.core.publisher.Mono;

public interface JwtService<T> {

    T extractTokenSubject(String token);

    Claims extractTokenClaims(String token);

    Mono<String> validateToken(String token);

    String generateToken(User user);
}
