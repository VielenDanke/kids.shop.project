package kz.danke.edge.service.configuration.security.jwt;

import io.jsonwebtoken.Claims;
import kz.danke.edge.service.document.User;

public interface JwtService<T> {

    T extractTokenSubject(String token);

    Claims extractTokenClaims(String token);

    boolean validateToken(String token);

    String generateToken(User user);
}
