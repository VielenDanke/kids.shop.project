package kz.danke.edge.service.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(
                        "clothes-get",
                        predicateSpec -> predicateSpec
                                .path("/clothes")
                                .and()
                                .method(HttpMethod.GET)
                                .filters(
                                        gatewayFilterSpec -> {
                                            gatewayFilterSpec.setPath("/api/v1/clothes");
                                            return gatewayFilterSpec;
                                        }
                                ).uri("lb://cloth-ms")
                )
                .route(
                        "clothes-post",
                        predicateSpec -> predicateSpec
                                .path("/clothes")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(
                                gatewayFilterSpec -> {
                                    gatewayFilterSpec.setPath("/api/v1/clothes");
                                    return gatewayFilterSpec;
                                }
                        )
                        .uri("lb://cloth-ms")
                )
                .build();
    }
}
