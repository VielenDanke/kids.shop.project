package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityApplied(
            ServerHttpSecurity httpSecurity,
            @Qualifier("userRedirectSuccessHandler") ServerAuthenticationSuccessHandler successHandler
    ) {
        httpSecurity
                .csrf()
                .disable()
                .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/registration").permitAll()
                .pathMatchers(HttpMethod.POST, "/login").permitAll()
                .pathMatchers("/login/oauth2/code/*").permitAll()
                .pathMatchers("/oauth2/authorization/*").permitAll()
                .pathMatchers("/oauth2/user/registration").permitAll()
                .pathMatchers(HttpMethod.GET, "/clothes/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/searching").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/*/files").permitAll() // remove after security would be done
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

    @Bean("userRedirectSuccessHandler")
    public ServerAuthenticationSuccessHandler userRedirectSuccessHandler(
            @Qualifier("userJwtService") JwtService<String> jwtService
    ) {
        UserServerAuthenticationSuccessHandler successHandler = new UserServerAuthenticationSuccessHandler();

        successHandler.setJwtService(jwtService);

        return successHandler;
    }
}
