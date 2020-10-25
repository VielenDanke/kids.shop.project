package kz.danke.edge.service.configuration.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service("userJwtService")
public class JwtServiceImpl implements JwtService<String> {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.expiration}")
    private String jwtExpiration;

    private final JsonObjectMapper jsonObjectMapper;

    @Autowired
    public JwtServiceImpl(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public String extractTokenSubject(String token) {
        return extractTokenClaims(token)
                .getSubject();
    }

    @Override
    public Claims extractTokenClaims(String token) {
        String secret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());

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
                .before(new Date());
    }

    @Override
    public String generateToken(Authentication authentication) {
        final String keyUserClaims = "user";
        final String oauth2User = "oauth2_user";

        HashMap<String, Object> claims = new HashMap<>();
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        String serializedUserDetails = jsonObjectMapper.serializeObject(principal);

        claims.put(keyUserClaims, null);
        claims.put(oauth2User, serializedUserDetails);

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + Long.parseLong(jwtExpiration) * 1000);
        String email = (String) principal.getAttributes().get("email");

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
