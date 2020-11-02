package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.service.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import kz.danke.edge.service.service.JsonObjectMapper;
import kz.danke.edge.service.service.ReactiveUserDetailsServiceImpl;
import kz.danke.edge.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean("userDetailsRepositoryReactiveAuthenticationManager")
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

    @Bean("userLogoutHandler")
    public ServerLogoutHandler userLogoutHandler(@Qualifier("userJwtService") JwtService<String> jwtService,
                                                 JsonObjectMapper jsonObjectMapper) {
        return new UserLogoutHandler(jwtService, jsonObjectMapper);
    }

    @Bean("oauth2UserRedirectSuccessHandler")
    public ServerAuthenticationSuccessHandler oauth2UserRedirectSuccessHandler(
            @Qualifier("userJwtService") JwtService<String> jwtService,
            ReactiveUserRepository reactiveUserRepository,
            JsonObjectMapper jsonObjectMapper
    ) {
        OAuthUserServerAuthenticationSuccessHandler successHandler = new OAuthUserServerAuthenticationSuccessHandler();

        successHandler.setJwtService(jwtService);
        successHandler.setReactiveUserRepository(reactiveUserRepository);
        successHandler.setJsonObjectMapper(jsonObjectMapper);

        return successHandler;
    }

    @Bean("oauth2UserRedirectFailureHandler")
    public ServerAuthenticationFailureHandler oauth2UserRedirectFailureHandler(JsonObjectMapper jsonObjectMapper) {
        return new OAuthUserServerAuthenticationFailureHandler(jsonObjectMapper);
    }

    @Bean("loggingFilter")
    public LoggingFilter loggingFilter(
            @Qualifier("userDetailsRepositoryReactiveAuthenticationManager") UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthenticationManager,
            @Qualifier("userJwtService") JwtService<String> jwtService,
            ReactiveUserRepository reactiveUserRepository,
            JsonObjectMapper jsonObjectMapper
    ) {
        LoggingFilter loggingFilter = new LoggingFilter(reactiveAuthenticationManager);

        UserServerAuthenticationSuccessHandler authenticationSuccessHandler = new UserServerAuthenticationSuccessHandler(jwtService);
        UserServerAuthenticationFailureHandler authenticationFailureHandler = new UserServerAuthenticationFailureHandler();

        authenticationSuccessHandler.setReactiveUserRepository(reactiveUserRepository);
        authenticationSuccessHandler.setJsonObjectMapper(jsonObjectMapper);
        authenticationFailureHandler.setJsonObjectMapper(jsonObjectMapper);

        loggingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        loggingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        return loggingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        final int strength = 8;

        return new BCryptPasswordEncoder(strength);
    }

    /*
    Post request on login with headers application/x-www-form-urlencoded

    because getFormData method on ServerWebRequest can read only with this header
     */
    @Bean
    public SecurityWebFilterChain securityApplied(
            ServerHttpSecurity httpSecurity,
            @Qualifier("oauth2UserRedirectSuccessHandler") ServerAuthenticationSuccessHandler oauth2SuccessHandler,
            @Qualifier("oauth2UserRedirectFailureHandler") ServerAuthenticationFailureHandler oauth2FailureHandler,
            @Qualifier("loggingFilter") LoggingFilter loggingFilter,
            UserAuthorizationTokenFilter tokenFilter,
            @Qualifier("userLogoutHandler") ServerLogoutHandler userLogoutHandler
    ) {
        httpSecurity
                .csrf()
                .disable()
                .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/registration").permitAll()
                .pathMatchers("/auth/login").permitAll()
                .pathMatchers("/login/oauth2/code/*").permitAll()
                .pathMatchers("/oauth2/authorization/*").permitAll()
                .pathMatchers("/oauth2/user/registration").permitAll()
                .pathMatchers(HttpMethod.GET, "/clothes/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/searching").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/*/files").hasRole(Authorities.ROLE_ADMIN.name()) // remove after security would be done
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .logout()
                .logoutHandler(userLogoutHandler)
                .and()
                .oauth2Login()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationSuccessHandler(oauth2SuccessHandler)
                .authenticationFailureHandler(oauth2FailureHandler)
                .and()
                .addFilterAt(loggingFilter, SecurityWebFiltersOrder.FORM_LOGIN)
                .addFilterAt(tokenFilter, SecurityWebFiltersOrder.AUTHORIZATION);

        return httpSecurity.build();
    }
}
