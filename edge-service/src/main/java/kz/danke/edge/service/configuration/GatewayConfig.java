package kz.danke.edge.service.configuration;

import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

@Configuration
@SuppressWarnings("unchecked")
public class GatewayConfig {

    @Bean("globalRetry")
    public RetryGatewayFilterFactory.RetryConfig globalRetry() {
        RetryGatewayFilterFactory.RetryConfig retryConfig = new RetryGatewayFilterFactory.RetryConfig();

        retryConfig.setMethods(HttpMethod.GET);
        retryConfig.setRetries(3);
        retryConfig.setExceptions(TimeoutException.class, IOException.class);
        retryConfig.setStatuses(HttpStatus.GATEWAY_TIMEOUT);

        RetryGatewayFilterFactory.BackoffConfig backoffConfig = new RetryGatewayFilterFactory.BackoffConfig();

        backoffConfig.setFactor(2);
        backoffConfig.setFirstBackoff(Duration.ofMillis(10));
        backoffConfig.setMaxBackoff(Duration.ofMillis(50));

        retryConfig.setBackoff(backoffConfig);

        return retryConfig;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {

        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        corsConfig.addAllowedHeader("*");

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
