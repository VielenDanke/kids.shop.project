package kz.danke.user.service.config.security;

import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
    public AuthFilter authFilter(JsonObjectMapper jsonObjectMapper, JwtService<String> jwtService) {
        ReactiveAuthenticationManager reactiveAuthenticationManager = new UserReactiveAuthenticationManager();

        AuthFilter authFilter = new AuthFilter(reactiveAuthenticationManager);

        authFilter.setAuthenticationConverter(new UserAuthenticationPathFilterConverter(jwtService, jsonObjectMapper));

        return authFilter;
    }

}
