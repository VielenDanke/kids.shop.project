package kz.danke.user.service.config.security;

import kz.danke.user.service.service.ReactiveUserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(PasswordEncoder passwordEncoder,
                                                                       ReactiveUserDetailsServiceImpl reactiveUserDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);

        userDetailsRepositoryReactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);
        userDetailsRepositoryReactiveAuthenticationManager.setUserDetailsPasswordService(reactiveUserDetailsService);

        return userDetailsRepositoryReactiveAuthenticationManager;
    }

    @Bean
    public ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver(
            ReactiveAuthenticationManager reactiveAuthenticationManager
    ) {
        return serverWebExchange -> Mono.just(reactiveAuthenticationManager);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity httpSecurity,
            LoggingFilter loggingFilter
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
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .oauth2Login()
                .and()
//                .logout(logout -> logout.logoutSuccessHandler(
//                        new OidcClientInitiatedServerLogoutSuccessHandler(reactiveClientRegistrationRepository)
//                ));
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .addFilterAt(loggingFilter, SecurityWebFiltersOrder.HTTP_BASIC);

        return httpSecurity.build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final int strength = 8;

        return new BCryptPasswordEncoder(strength);
    }
}
