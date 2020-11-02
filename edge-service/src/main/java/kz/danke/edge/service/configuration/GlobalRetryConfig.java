package kz.danke.edge.service.configuration;

import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
@SuppressWarnings("unchecked")
public class GlobalRetryConfig {

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
}
