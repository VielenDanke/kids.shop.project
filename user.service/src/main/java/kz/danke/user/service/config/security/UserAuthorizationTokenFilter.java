package kz.danke.user.service.config.security;

import io.jsonwebtoken.Claims;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserAuthorizationTokenFilter implements WebFilter {

    private final JwtService<String> jwtService;
    private final JsonObjectMapper jsonObjectMapper;

    @Autowired
    public UserAuthorizationTokenFilter(@Qualifier("userJwtService") JwtService<String> jwtService,
                                        JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        final String authorizationHeaderName = "Authorization";
        final int firstAcceptedAuthorizationHeaderIndex = 0;

        List<String> tokens = serverWebExchange.getRequest().getHeaders().get(authorizationHeaderName);

        if (tokens == null || tokens.isEmpty()) {
            return webFilterChain.filter(serverWebExchange);
        }
        String token = tokens.get(firstAcceptedAuthorizationHeaderIndex);

        if (StringUtils.isEmpty(token)) {
            return webFilterChain.filter(serverWebExchange);
        }
        if (jwtService.validateToken(token)) {
            Claims claims = jwtService.extractTokenClaims(token);

            String userDetailsJson = claims.get("user", String.class);

            UserDetailsImpl userDetails = jsonObjectMapper.deserializeJson(userDetailsJson, UserDetailsImpl.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            ReactiveSecurityContextHolder.withAuthentication(authToken);
        }
        return webFilterChain.filter(serverWebExchange);
    }
}
