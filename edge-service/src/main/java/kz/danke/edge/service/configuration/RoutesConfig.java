package kz.danke.edge.service.configuration;

import kz.danke.edge.service.configuration.security.filter.AuthorizationHeaderFilter;
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
                                      @Qualifier("globalRetry") RetryGatewayFilterFactory.RetryConfig globalRetryConfig,
                                      AuthorizationHeaderFilter authorizationHeaderFilter) {
        return builder
                .routes()
                .route(
                        "clothes-get",
                        getClothFindAllPredicate -> getClothFindAllPredicate
                                .method(HttpMethod.GET)
                                .and()
                                .path("/clothes")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec
                                                .retry(retryConfig -> retryConfig = globalRetryConfig)
                                )
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
                                    gatewayFilterSpec.filter(
                                            authorizationHeaderFilter.apply(
                                                    new AuthorizationHeaderFilter.Config()
                                            )
                                    );
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
                                        }).filter(authorizationHeaderFilter.apply(
                                                new AuthorizationHeaderFilter.Config()
                                        ))
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
                        "get-clothes-cart",
                        getClothesCart -> getClothesCart
                                .method(HttpMethod.POST)
                                .and()
                                .path("/clothes/cart")
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
                                }).filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "get-all-promotions",
                        getAllPromotions -> getAllPromotions
                                .method(HttpMethod.GET)
                                .and()
                                .path("/promotions")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.retry(retryConfig -> retryConfig = globalRetryConfig))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "add-new-promotions",
                        addNewPromotions -> addNewPromotions
                                .method(HttpMethod.POST)
                                .and()
                                .path("/promotions")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }).filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                                .uri("lb://cloth-ms")
                )
                .route(
                        "add-file-to-promotion",
                        addFileToPromotion -> addFileToPromotion
                                .method(HttpMethod.POST)
                                .and()
                                .path("/promotions/*/file")
                                .filters(
                                        clothAddFilesFilter -> clothAddFilesFilter.rewritePath(
                                                "/promotions/(?<segment>.*)/file",
                                                "/promotions/${segment}/file"
                                        ).retry(retryConfig -> {
                                            retryConfig = globalRetryConfig;
                                            retryConfig.setMethods(HttpMethod.POST);
                                        }).filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config()))
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "delete-promotion-by-id",
                        deletePromotionById -> deletePromotionById
                                .method(HttpMethod.DELETE)
                                .and()
                                .path("/promotions/*")
                                .filters(
                                        deletePromotionFilter -> deletePromotionFilter.rewritePath(
                                                "/promotions/(?<segment>.*)",
                                                "/promotions/${segment}"
                                        ).retry(retryConfig -> {
                                            retryConfig = globalRetryConfig;
                                            retryConfig.setMethods(HttpMethod.DELETE);
                                        }).filter(
                                                authorizationHeaderFilter.apply(
                                                        new AuthorizationHeaderFilter.Config()
                                                )
                                        )
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "delete-cloth-by-id",
                        deleteClothById -> deleteClothById
                                .method(HttpMethod.DELETE)
                                .and()
                                .path("/clothes/*")
                                .filters(
                                        deletePromotionFilter -> deletePromotionFilter.rewritePath(
                                                "/clothes/(?<segment>.*)",
                                                "/clothes/${segment}"
                                        ).retry(retryConfig -> {
                                            retryConfig = globalRetryConfig;
                                            retryConfig.setMethods(HttpMethod.DELETE);
                                        }).filter(
                                                authorizationHeaderFilter.apply(
                                                        new AuthorizationHeaderFilter.Config()
                                                )
                                        )
                                )
                                .uri("lb://cloth-ms")
                )
                .route(
                        "user-cart-decline",
                        userCartDecline -> userCartDecline
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cart/reserve/decline")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://user-ms")
                )
                .route(
                        "user-cart-validate",
                        userCartValidate -> userCartValidate
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cart/reserve")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://user-ms")
                )
                .route(
                        "user-cart-valid-retrieve",
                        userCartValidRetrieve -> userCartValidRetrieve
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cart/retrieve")
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
                .route(
                        "get-user-cabinet",
                        getUserCabinet -> getUserCabinet
                                .method(HttpMethod.GET)
                                .and()
                                .path("/cabinet")
                                .filters(gatewayFilterSpec ->
                                        gatewayFilterSpec.retry(retryConfig -> retryConfig = globalRetryConfig)
                                .filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                                .uri("lb://user-ms")
                )
                .route(
                        "save-new-user",
                        saveNewUser -> saveNewUser
                                .method(HttpMethod.POST)
                                .and()
                                .path("/auth/registration")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }))
                                .uri("lb://user-ms")
                )
                .route(
                        "login-user",
                        loginUser -> loginUser
                        .method(HttpMethod.POST)
                        .and()
                        .path("/auth/login")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                            retryConfig = globalRetryConfig;
                            retryConfig.setMethods(HttpMethod.POST);
                        }))
                        .uri("lb://user-ms")
                )
                .route(
                        "update-user",
                        updateUser -> updateUser
                                .method(HttpMethod.POST)
                                .and()
                                .path("/cabinet/update")
                                .filters(gatewayFilterSpec -> gatewayFilterSpec.retry(retryConfig -> {
                                    retryConfig = globalRetryConfig;
                                    retryConfig.setMethods(HttpMethod.POST);
                                }).filter(authorizationHeaderFilter.apply(new AuthorizationHeaderFilter.Config())))
                                .uri("lb://user-ms")
                )
                .build();
    }
}
