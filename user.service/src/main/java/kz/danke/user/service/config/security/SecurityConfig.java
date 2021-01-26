package kz.danke.user.service.config.security;

import kz.danke.user.service.config.security.filters.AuthFilter;
import kz.danke.user.service.config.security.filters.LoggingFilter;
import kz.danke.user.service.config.security.handlers.*;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.impl.UserDetailsPasswordServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean("userDetailsRepositoryReactiveAuthenticationManager")
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(
            PasswordEncoder passwordEncoder,
            UserDetailsPasswordServiceImpl userDetailsPasswordService
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsPasswordService);

        userDetailsRepositoryReactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);
        userDetailsRepositoryReactiveAuthenticationManager.setUserDetailsPasswordService(userDetailsPasswordService);

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
                                                 JsonObjectMapper jsonObjectMapper,
                                                 @Value("${auth.token.key}") String accessTokenKey,
                                                 @Value("${auth.roles.key}") String authRolesKey,
                                                 @Value("${user.claims.key}") String userClaimsKey) {
        UserLogoutHandler userLogoutHandler = new UserLogoutHandler(jwtService, jsonObjectMapper);

        userLogoutHandler.setAccessTokenKey(accessTokenKey);
        userLogoutHandler.setRolesKey(authRolesKey);
        userLogoutHandler.setUserClaimsKey(userClaimsKey);

        return userLogoutHandler;
    }

    @Bean("oauth2UserRedirectSuccessHandler")
    public ServerAuthenticationSuccessHandler oauth2UserRedirectSuccessHandler(
            @Qualifier("userJwtService") JwtService<String> jwtService,
            ReactiveUserRepository reactiveUserRepository,
            JsonObjectMapper jsonObjectMapper,
            @Value("${auth.token.key}") String accessTokenKey,
            @Value("${auth.roles.key}") String authRolesKey
    ) {
        OAuthUserServerAuthenticationSuccessHandler successHandler = new OAuthUserServerAuthenticationSuccessHandler();

        successHandler.setJwtService(jwtService);
        successHandler.setReactiveUserRepository(reactiveUserRepository);
        successHandler.setJsonObjectMapper(jsonObjectMapper);
        successHandler.setAuthRolesKey(authRolesKey);
        successHandler.setAuthTokenKey(accessTokenKey);

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
            JsonObjectMapper jsonObjectMapper,
            @Value("${auth.token.key}") String accessTokenKey,
            @Value("${auth.roles.key}") String authRolesKey
    ) {
        LoggingFilter loggingFilter = new LoggingFilter(reactiveAuthenticationManager);

        UserServerAuthenticationSuccessHandler authenticationSuccessHandler = new UserServerAuthenticationSuccessHandler(jwtService, accessTokenKey, authRolesKey);
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

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity httpSecurity,
            AuthFilter authFilter,
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
                .pathMatchers(HttpMethod.POST, "/cart/reserve").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/process").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/reserve/decline").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/retrieve").permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .addFilterAt(authFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(loggingFilter, SecurityWebFiltersOrder.FORM_LOGIN)
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
        String[] postMatchers = new String[]{
                "/cabinet/update"
        };
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
