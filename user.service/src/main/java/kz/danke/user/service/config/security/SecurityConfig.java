package kz.danke.user.service.config.security;

import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        final int strength = 8;

        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity httpSecurity,
            AuthFilter authFilter
    ) {
        httpSecurity
                .csrf()
                .disable()
                .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/registration").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/reserve").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/process").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/reserve/decline").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/retrieve").permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .addFilterAt(authFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .logout()
                .disable();

        return httpSecurity.build();
    }

    @Bean
    public AuthFilter authFilter(JsonObjectMapper jsonObjectMapper,
                                 JwtService<String> jwtService,
                                 @Value("${auth.token.key}") String accessTokenKey,
                                 @Value("${user.claims.key}") String userClaimsKey
    ) {
        ReactiveAuthenticationManager reactiveAuthenticationManager = new UserReactiveAuthenticationManager();

        String[] getMatchers = new String[]{
                "/cabinet"
        };

        String[] postMatchers = new String[]{};

        String[] deleteMatchers = new String[]{

        };

        AuthFilter authFilter = new AuthFilter(reactiveAuthenticationManager);

        UserAuthenticationPathFilterConverter authenticationConverter = new UserAuthenticationPathFilterConverter(jwtService, jsonObjectMapper);

        authenticationConverter.setAccessTokenKey(accessTokenKey);
        authenticationConverter.setUserClaimsKey(userClaimsKey);

        authFilter.setAuthenticationConverter(authenticationConverter);

        authFilter.setServerWebExchangeMatherWithPathMatchers(getMatchers, postMatchers, deleteMatchers);

        return authFilter;
    }

}
