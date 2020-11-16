package kz.danke.edge.service.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,
                                      @Qualifier("globalRetry") RetryGatewayFilterFactory.RetryConfig globalRetryConfig) {
        return builder
                .routes()
                .route(
                        "clothes-get",
                        getClothFindAllPredicate -> getClothFindAllPredicate
                                .method(HttpMethod.GET)
                                .and()
                                .path("/clothes")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.retry(retryConfig -> retryConfig = globalRetryConfig))
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
                                        ).retry(retryConfig -> retryConfig = globalRetryConfig)
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "clothes-post",
                        getClothSaveNewPredicate -> getClothSaveNewPredicate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes")
                                .filters(gatewayFilterSpec -> {
                                    gatewayFilterSpec.retry(retryConfig -> {
                                        retryConfig = globalRetryConfig;
                                        retryConfig.setMethods(HttpMethod.POST);
                                    });
                                    return gatewayFilterSpec;
                                })
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
                                        ).retry(retryConfig -> {
                                            retryConfig = globalRetryConfig;
                                            retryConfig.setMethods(HttpMethod.POST);
                                        })
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "cloth-text-searching",
                        clothTextSearching -> clothTextSearching
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes/searching")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "get-all-categories",
                        getAllCategories -> getAllCategories
                                .method(HttpMethod.GET)
                                .and()
                                .path("/categories")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.retry(retryConfig -> retryConfig = globalRetryConfig))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "add-category",
                        clothAddCategory -> clothAddCategory
                                .method(HttpMethod.POST)
                                .and()
                                .path("/categories")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "user-cart-validate",
                        userCartValidate -> userCartValidate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cart/validate")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://user-ms")
                )
                .route(
                        "user-cart-process",
                        userCartProcess -> userCartProcess
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cart/process")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://user-ms")
                )
                .build();
    }
}
