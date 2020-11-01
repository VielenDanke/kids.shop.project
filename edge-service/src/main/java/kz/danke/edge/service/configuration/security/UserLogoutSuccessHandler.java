package kz.danke.edge.service.configuration.security;

import io.jsonwebtoken.Claims;
import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.dto.response.LogoutResponse;
import kz.danke.edge.service.service.JsonObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

// Need to modified a little bit
public class UserLogoutSuccessHandler implements ServerLogoutSuccessHandler {

    private final JwtService<String> jwtService;
    private final JsonObjectMapper jsonObjectMapper;

    public UserLogoutSuccessHandler(JwtService<String> jwtService, JsonObjectMapper jsonObjectMapper) {
        this.jwtService = jwtService;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange exchange, Authentication authentication) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.getAuthentication().setAuthenticated(false))
                .then(Mono.just(exchange))
                .flatMap(webFilterExchange -> {
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

                    HttpHeaders headers = request.getHeaders();

                    List<String> authorizationHeaders = headers.get(HttpHeaders.AUTHORIZATION);

                    if (authorizationHeaders == null) {
                        return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                    }
                    String authorizationHeader = authorizationHeaders.get(0);

                    if (!jwtService.validateToken(authorizationHeader)) {
                        return webFilterExchange.getChain().filter(webFilterExchange.getExchange());
                    }

                    Claims claims = jwtService.extractTokenClaims(authorizationHeader);

                    String user = claims.get("user", String.class);

                    User userFromAuthorizationHeader = jsonObjectMapper.deserializeJson(user, User.class);

                    LogoutResponse logoutResponse = new LogoutResponse(
                            userFromAuthorizationHeader.getId(),
                            userFromAuthorizationHeader.getUsername()
                    );

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    String logoutResponseJson = jsonObjectMapper.serializeObject(logoutResponse);

                    DataBuffer wrappedLogoutResponse = dataBufferFactory
                            .wrap(logoutResponseJson.getBytes(StandardCharsets.UTF_8));

                    HttpHeaders responseHeaders = response.getHeaders();

                    responseHeaders.add(HttpHeaders.AUTHORIZATION, "");
                    responseHeaders.add("Roles", "");
                    response.setRawStatusCode(200);
                    responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    return response.writeWith(Flux.just(wrappedLogoutResponse));
                });
    }
}
