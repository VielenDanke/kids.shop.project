package kz.danke.edge.service.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RoutesConfig {

    /*
    Need to read about Routing in Reactor
     */

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(
                        "clothes-get",
                        getClothFindAllPredicate -> getClothFindAllPredicate
                                .method(HttpMethod.GET)
                                .and()
                                .path("/clothes")
                                .filters(f -> f.rewritePath("/clothes", "/api/v1/clothes"))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "clothes-post",
                        getClothSaveNewPredicate -> getClothSaveNewPredicate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes")
                                .filters(
                                        clothesPostFilter ->
                                                clothesPostFilter.rewritePath(
                                                        "/clothes",
                                                        "/api/v1/clothes"
                                                )
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "user-login",
                        getUserLoginPredicate -> getUserLoginPredicate
                        .method(HttpMethod.POST)
                        .and()
                        .path("/login")
                        .filters(
                                userLoginPostFilter ->
                                        userLoginPostFilter.rewritePath(
                                                "/login", "/auth/login")
                        )
                        .uri("lb://user-ms")
                )
                .route(
                        "user-registration",
                        getUserLoginPredicate -> getUserLoginPredicate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/registration")
                                .filters(
                                        userLoginPostFilter ->
                                                userLoginPostFilter.rewritePath(
                                                        "/registration", "/auth/registration")
                                )
                                .uri("lb://user-ms")
                )
                .build();
    }
}
