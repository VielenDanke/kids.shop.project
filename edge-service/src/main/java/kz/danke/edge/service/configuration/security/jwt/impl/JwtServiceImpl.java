package kz.danke.edge.service.configuration.security.jwt.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kz.danke.edge.service.configuration.AppConfigProperties;
import kz.danke.edge.service.configuration.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service("userJwtService")
public class JwtServiceImpl implements JwtService<String> {

    private final AppConfigProperties properties;

    @Value("${user.claims.key}")
    private String userClaimsKey;

    @Autowired
    public JwtServiceImpl(AppConfigProperties properties) {
        this.properties = properties;
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
}
