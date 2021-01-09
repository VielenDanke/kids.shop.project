package kz.danke.user.service.config.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.document.User;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service("userJwtService")
public class JwtServiceImpl implements JwtService<String> {

    private final AppConfigProperties properties;
    private final JsonObjectMapper jsonObjectMapper;

    @Value("${user.claims.key}")
    private String userClaimsKey;

    @Autowired
    public JwtServiceImpl(AppConfigProperties properties,
                          JsonObjectMapper jsonObjectMapper) {
        this.properties = properties;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public String extractTokenSubject(String token) {
        return extractTokenClaims(token)
                .getSubject();
    }

    @Override
    public Claims extractTokenClaims(String token) {
        String secret = Base64.getEncoder().encodeToString(properties.getJwt().getSecret().getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Mono<String> validateToken(String token) {
        return Mono.justOrEmpty(token)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Empty token"))))
                .filter(t -> extractTokenClaims(t)
                .getExpiration()
                .after(new Date()));
    }

    @Override
    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();

        String serializedUser = jsonObjectMapper.serializeObject(user);

        claims.put(userClaimsKey, serializedUser);

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + properties.getJwt().getExpiration() * 1000);

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes()))
                .compact();
    }
}
