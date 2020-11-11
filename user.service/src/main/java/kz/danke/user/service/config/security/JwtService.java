package kz.danke.user.service.config.security;

import io.jsonwebtoken.Claims;
import kz.danke.user.service.document.User;

public interface JwtService<T> {

    T extractTokenSubject(String token);

    Claims extractTokenClaims(String token);

    boolean validateToken(String token);

    String generateToken(User user);
}
