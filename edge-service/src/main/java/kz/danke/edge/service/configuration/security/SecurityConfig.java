package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.configuration.security.converter.UserAuthenticationPathFilterConverter;
import kz.danke.edge.service.configuration.security.filter.AuthFilter;
import kz.danke.edge.service.configuration.security.filter.LoggingFilter;
import kz.danke.edge.service.configuration.security.handler.*;
import kz.danke.edge.service.configuration.security.jwt.JwtService;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import kz.danke.edge.service.service.JsonObjectMapper;
import kz.danke.edge.service.service.ReactiveUserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean("userDetailsRepositoryReactiveAuthenticationManager")
    public UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager(
            PasswordEncoder passwordEncoder,
            ReactiveUserDetailsServiceImpl reactiveUserDetailsService
    ) {
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
    public AuthFilter authFilter(JsonObjectMapper jsonObjectMapper,
                                 JwtService<String> jwtService,
                                 @Value("${auth.token.key}") String accessTokenKey,
                                 @Value("${user.claims.key}") String userClaimsKey
    ) {
        ReactiveAuthenticationManager reactiveAuthenticationManager = new UserReactiveAuthenticationManager();
        UserServerAuthenticationFailureHandler serverAuthenticationFailureHandler = new UserServerAuthenticationFailureHandler();

        serverAuthenticationFailureHandler.setJsonObjectMapper(jsonObjectMapper);

        AuthFilter authFilter = new AuthFilter(reactiveAuthenticationManager);

        String[] getMatchers = new String[]{
                "/cabinet"
        };
        String[] postMatchers = new String[]{
                "/clothes/*/files",
                "/clothes",
                "/categories",
                "/promotions",
                "/promotions/*/file"
        };
        String[] deleteMatchers = new String[]{
                "/promotions/*",
                "/clothes/*"
        };

        authFilter.setServerWebExchangeMatherWithPathMatchers(getMatchers, postMatchers, deleteMatchers);
        authFilter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler);
        authFilter.setAuthenticationConverter(new UserAuthenticationPathFilterConverter(jwtService, jsonObjectMapper, accessTokenKey, userClaimsKey));

        return authFilter;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain securityApplied(
            ServerHttpSecurity httpSecurity,
            @Qualifier("oauth2UserRedirectSuccessHandler") ServerAuthenticationSuccessHandler oauth2SuccessHandler,
            @Qualifier("oauth2UserRedirectFailureHandler") ServerAuthenticationFailureHandler oauth2FailureHandler,
            @Qualifier("loggingFilter") LoggingFilter loggingFilter,
            @Qualifier("userLogoutHandler") ServerLogoutHandler userLogoutHandler,
            AuthFilter authFilter,
            CorsWebFilter corsWebFilter
    ) {
        httpSecurity
                .csrf()
                .disable()
                .authorizeExchange()
                .matchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/registration").permitAll()
                .pathMatchers("/auth/login").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/reserve").permitAll()
                .pathMatchers(HttpMethod.POST, "/cart/process").permitAll()
                .pathMatchers("/login/oauth2/code/*").permitAll()
                .pathMatchers("/oauth2/authorization/*").permitAll()
                .pathMatchers("/oauth2/user/registration").permitAll()
                .pathMatchers(HttpMethod.GET, "/categories").permitAll()
                .pathMatchers(HttpMethod.GET, "/clothes/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/promotions").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/searching").permitAll()
                .pathMatchers(HttpMethod.POST, "/clothes/cart").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/clothes/*").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/clothes").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/promotions").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/promotions/*/file").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/categories").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.DELETE, "/promotions/*").hasAuthority(Authorities.ROLE_ADMIN.name())
                .pathMatchers(HttpMethod.POST, "/clothes/*/files").hasAuthority(Authorities.ROLE_ADMIN.name()) // remove after security would be done
                .anyExchange()
                .authenticated()
                .and()
                .addFilterAt(authFilter, SecurityWebFiltersOrder.HTTP_BASIC)
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
                .addFilterAt(corsWebFilter, SecurityWebFiltersOrder.CORS)
                .addFilterAt(loggingFilter, SecurityWebFiltersOrder.FORM_LOGIN);

        return httpSecurity.build();
    }
}
