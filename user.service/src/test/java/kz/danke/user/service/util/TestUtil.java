package kz.danke.user.service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kz.danke.user.service.document.User;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public class TestUtil {

    public static String generateToken(User user, Date date, String secret) throws JsonProcessingException {
        final String userClaimsKey = "user";

        HashMap<String, Object> claims = new HashMap<>();

        String serializedUser = new ObjectMapper().writeValueAsString(user);

        claims.put(userClaimsKey, serializedUser);

        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + (date == null ? 0 : date.getTime()));

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
