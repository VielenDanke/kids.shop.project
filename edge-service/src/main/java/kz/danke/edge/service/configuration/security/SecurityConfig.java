package kz.danke.edge.service.configuration.security;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityApplied(
            ServerHttpSecurity httpSecurity,
            UserRedirectServerAuthenticationSuccessHandler successHandler
    ) {
        httpSecurity
                .csrf()
                .disable()
                .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/registration").permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .pathMatchers("/login/oauth2/code/*").permitAll()
                .pathMatchers("/oauth2/authorization/*").permitAll()
                .pathMatchers("/oauth2/user/registration").permitAll()
                .pathMatchers(HttpMethod.GET, "/clothes/**").permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .oauth2Login()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationSuccessHandler(successHandler);

        return httpSecurity.build();
    }

    @Bean
    public UserRedirectServerAuthenticationSuccessHandler successHandler() {
        return new UserRedirectServerAuthenticationSuccessHandler("/");
    }
}
