package kz.danke.edge.service.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(
                        "clothes",
                        predicateSpec -> predicateSpec.path("/clothes").filters(
                                gatewayFilterSpec -> {
                                    gatewayFilterSpec.setPath("/api/v1/clothes");
                                    return gatewayFilterSpec;
                                }
                        ).uri("lb://cloth-ms")
                ).build();
    }
}
