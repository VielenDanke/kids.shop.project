package kz.danke.edge.service.configuration.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.edge.service.configuration.AppConfigProperties;
import kz.danke.edge.service.configuration.security.jwt.JwtService;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service("userJwtService")
public class JwtServiceImpl implements JwtService<String> {

    private final AppConfigProperties properties;
    private final JsonObjectMapper jsonObjectMapper;

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
    public boolean validateToken(String token) {
        return extractTokenClaims(token)
                .getExpiration()
                .after(new Date());
    }

    @Override
    public String generateToken(User user) {
        final String keyUserClaims = "user";

        HashMap<String, Object> claims = new HashMap<>();

        String serializedUser = jsonObjectMapper.serializeObject(user);

        claims.put(keyUserClaims, serializedUser);

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
