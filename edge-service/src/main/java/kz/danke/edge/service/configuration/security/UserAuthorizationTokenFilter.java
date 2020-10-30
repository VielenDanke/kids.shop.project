package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.service.JsonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
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
        final String userClaimsKey = "user";

        List<String> headers = serverWebExchange.getRequest().getHeaders().get(authorizationHeaderName);

        Mono<SecurityContext> securityContext = ReactiveSecurityContextHolder.getContext();

        if (headers != null) {
            return Flux.fromIterable(headers)
                    .filter(token -> token != null && !token.isEmpty())
                    .filter(jwtService::validateToken)
                    .map(jwtService::extractTokenClaims)
                    .map(claims -> claims.get(userClaimsKey))
                    .map(userClaims -> {
                        UserDetailsImpl userDetails = jsonObjectMapper.deserializeJson((String) userClaims, UserDetailsImpl.class);

                        if (userDetails == null) {
                            DefaultOAuth2User oAuth2User = jsonObjectMapper.deserializeJson((String) userClaims, DefaultOAuth2User.class);
                            return new UsernamePasswordAuthenticationToken(
                                    oAuth2User.getAttribute("email"),
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority(Authorities.ROLE_GUEST.name()))
                            );
                        }
                        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    })
                    .zipWith(securityContext)
                    .doOnNext(tuple -> {
                        UsernamePasswordAuthenticationToken token = tuple.getT1();
                        SecurityContext context = tuple.getT2();
                        context.setAuthentication(token);
                    })
                    .then(webFilterChain.filter(serverWebExchange));
        }
        return webFilterChain.filter(serverWebExchange);
    }
}
