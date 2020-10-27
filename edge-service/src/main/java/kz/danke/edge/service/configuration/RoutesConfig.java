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
                        getClothFindAllPredicate -> getClothFindAllPredicate
                                .method(HttpMethod.GET)
                                .and()
                                .path("/clothes")
                                .uri("lb://cloth-ms")
                )
                .route(
                        "clothes-get-id",
                        getClothByIdPredicate -> getClothByIdPredicate
                                .method(HttpMethod.GET)
                                .and()
                                .path("/clothes/*")
                                .filters(
                                        clothByIdFilter -> clothByIdFilter.rewritePath(
                                                "/clothes/(?<segment>.*)",
                                                "/clothes/${segment}"
                                        )
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "clothes-post",
                        getClothSaveNewPredicate -> getClothSaveNewPredicate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes")
                                .uri("lb://cloth-ms")
                )
                .route(
                        "clothes-add-files",
                        clothAddFiles -> clothAddFiles
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes/*/files")
                                .filters(
                                        clothAddFilesFilter -> clothAddFilesFilter.rewritePath(
                                                "/clothes/(?<segment>.*)/files",
                                                "/clothes/${segment}/files"
                                        )
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "cloth-text-searching",
                        clothTextSearching -> clothTextSearching
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes/searching")
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
                                                        "/login",
                                                        "/auth/login")
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
                                                        "/registration",
                                                        "/auth/registration")
                                )
                                .uri("lb://user-ms")
                )
                .build();
    }
}
