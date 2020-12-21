package kz.danke.kids.shop.config.router;

import kz.danke.kids.shop.config.handler.CategoryHandler;
import kz.danke.kids.shop.config.handler.ClothHandler;
import kz.danke.kids.shop.config.handler.PromotionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ClothRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> clothRouterFunction(
            ClothHandler clothHandler, CategoryHandler categoryHandler, PromotionHandler promotionHandler
    ) {
        return RouterFunctions.route()
                .path("/clothes", builder -> builder
                        .GET("", clothHandler::handleMainPageClothes)
                        .POST("", clothHandler::handleClothSaving)
                        .DELETE("/{id}", clothHandler::deleteClothById)
                        .GET("/{id}", clothHandler::handleClothById)
                        .POST("/cart", clothHandler::handleClothCart)
                        .POST("/{id}/files", clothHandler::handleFileSaving)
                        .POST("/searching", clothHandler::handleClothTextSearching)
                        .POST("/reserve", clothHandler::processDeclineOrReserve)
                        .POST("/reserve/decline", clothHandler::processDeclineOrReserve)
                )
                .build()
                .andRoute(
                        RequestPredicates.POST("/categories"),
                        categoryHandler::addCategory
                ).andRoute(
                        RequestPredicates.GET("/categories"),
                        categoryHandler::finAllCategories
                ).andRoute(
                        RequestPredicates.POST("/promotions"),
                        promotionHandler::handleSavePromotion
                ).andRoute(
                        RequestPredicates.GET("/promotions"),
                        promotionHandler::getAllPromotions
                ).andRoute(
                        RequestPredicates.POST("/promotions/{id}/file"),
                        promotionHandler::handleSaveFileToPromotion
                ).andRoute(
                        RequestPredicates.DELETE("/promotions/{id}"),
                        promotionHandler::handleDeletePromotion
                );
    }
}
