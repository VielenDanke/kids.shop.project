package kz.danke.user.service.config.security.handlers;

import io.jsonwebtoken.Claims;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.response.LogoutResponse;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserLogoutHandler implements ServerLogoutHandler {

    private final JwtService<String> jwtService;
    private final JsonObjectMapper jsonObjectMapper;
    private String accessTokenKey;
    private String rolesKey;
    private String userClaimsKey;

    private ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.pathMatchers("/logout");

    public UserLogoutHandler(JwtService<String> jwtService, JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }


    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        return this.matcher.matches(exchange.getExchange())
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .switchIfEmpty(exchange.getChain().filter(exchange.getExchange()).then(Mono.empty()))
                .flatMap(matchResult -> this.logoutHandler(exchange, authentication));
    }

    private Mono<Void> logoutHandler(WebFilterExchange exchange, Authentication authentication) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.getAuthentication().setAuthenticated(false))
                .then(Mono.just(exchange))
                .flatMap(webFilterExchange -> {
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

                    HttpHeaders responseHeaders = response.getHeaders();
                    HttpHeaders requestHeaders = request.getHeaders();

                    List<String> authorizationHeaderList = requestHeaders.get(HttpHeaders.AUTHORIZATION);

                    if (authorizationHeaderList == null) {
                        return exchange.getChain().filter(exchange.getExchange());
                    }
                    String authorizationHeader = authorizationHeaderList.get(0);

                    Claims claims = jwtService.extractTokenClaims(authorizationHeader);

                    String user = claims.get("user", String.class);

                    User userFromJson = jsonObjectMapper.deserializeJson(user, User.class);

                    LogoutResponse logoutResponse = new LogoutResponse(
                            userFromJson.getId(),
                            userFromJson.getUsername()
                    );

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    String logoutResponseJson = jsonObjectMapper.serializeObject(logoutResponse);

                    DataBuffer wrappedLogoutResponse = dataBufferFactory
                            .wrap(logoutResponseJson.getBytes(StandardCharsets.UTF_8));

                    responseHeaders.remove("accessToken");
                    responseHeaders.remove("roles");
                    responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    return response.writeWith(Flux.just(wrappedLogoutResponse));
                });
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public void setRolesKey(String rolesKey) {
        this.rolesKey = rolesKey;
    }

    public void setUserClaimsKey(String userClaimsKey) {
        this.userClaimsKey = userClaimsKey;
    }
}
