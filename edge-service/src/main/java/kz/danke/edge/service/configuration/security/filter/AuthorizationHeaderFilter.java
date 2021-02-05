package kz.danke.edge.service.configuration.security.filter;

import kz.danke.edge.service.configuration.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${auth.token.key}")
    private String accessTokenKey;
    private JwtService<String> jwtService;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey(accessTokenKey)) {
                return onError(exchange, String.format("No %s header", accessTokenKey), HttpStatus.UNAUTHORIZED);
            }
            String token = headers.getFirst(accessTokenKey);
            String notValidMessage = String.format("%s header is not valid", accessTokenKey);
            try {
                boolean isValid = jwtService.validateToken(token);
                if (!isValid) {
                    return onError(exchange, notValidMessage, HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                return onError(exchange, notValidMessage, HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String format, HttpStatus unauthorized) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(unauthorized);
        return response.setComplete();
    }

    @Autowired
    public void setJwtService(JwtService<String> jwtService) {
        this.jwtService = jwtService;
    }
}
