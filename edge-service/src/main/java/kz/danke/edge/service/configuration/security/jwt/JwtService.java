package kz.danke.edge.service.configuration.security.jwt;

import io.jsonwebtoken.Claims;

public interface JwtService<T> {

    Claims extractTokenClaims(String token);

    boolean validateToken(String token);
}
