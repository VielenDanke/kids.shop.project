package kz.danke.edge.service.configuration.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.edge.service.configuration.AppConfigProperties;
import kz.danke.edge.service.configuration.security.UserDetailsImpl;
import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.exception.UnknownPrincipalException;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
                .before(new Date());
    }

    @Override
    public String generateToken(Authentication authentication) {
        final String keyUserClaims = "user";
        final String subject;
        final Object principal = authentication.getPrincipal();

        HashMap<String, Object> claims = new HashMap<>();
        String serializedUserDetails = jsonObjectMapper.serializeObject(principal);

        claims.put(keyUserClaims, serializedUserDetails);

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + properties.getJwt().getExpiration() * 1000);

        if (principal.getClass().isAssignableFrom(DefaultOAuth2User.class)) {
            subject = (String) ((DefaultOAuth2User) principal).getAttributes().get("email");
        } else if (principal.getClass().isAssignableFrom(UserDetailsImpl.class)) {
            subject = ((UserDetailsImpl) principal).getUsername();
        } else {
            throw new UnknownPrincipalException("Principal is unknown");
        }

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes()))
                .compact();
    }
}
