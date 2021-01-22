package kz.danke.edge.service.configuration.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class GlobalFilterConfiguration {

    @Configuration
    public static class RequestTimerGlobalFilterConfiguration {

        private static final Logger LOGGER = LoggerFactory.getLogger(RequestTimerGlobalFilterConfiguration.class);
        private int startTime;

        @Bean
        public GlobalFilter timerPreFilter() {
            startTime = LocalDateTime.now().getNano();
            return (exchange, chain) -> chain.filter(exchange);
        }

        @Bean
        public GlobalFilter timerPostFilter() {
            return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
                LOGGER.info(
                        String.format(
                                "Request done in %d with PATH: %s, METHOD: %s",
                                LocalDateTime.now().getNano() - startTime,
                                exchange.getRequest().getURI().getPath(),
                                exchange.getRequest().getMethod().name()
                        )
                );
            }));
        }
    }
}
