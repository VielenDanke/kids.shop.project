package kz.danke.user.service.config.security;

import io.jsonwebtoken.Claims;

public interface JwtService<T> {

    T extractTokenSubject(String token);

    Claims extractTokenClaims(String token);

    boolean validateToken(String token);

    String generateToken(UserDetailsImpl userDetails);
}
