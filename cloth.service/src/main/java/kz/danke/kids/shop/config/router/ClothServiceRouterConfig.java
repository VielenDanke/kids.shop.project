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

import javax.validation.constraints.NotNull;

@Configuration
public class ClothServiceRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> clothServiceRouterFunctions(
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
                .path("/categories", builder -> builder
                        .GET("", categoryHandler::finAllCategories)
                        .POST("", categoryHandler::addCategory)
                )
                .path("/promotions", builder -> builder
                        .POST("", promotionHandler::handleSavePromotion)
                        .GET("", promotionHandler::getAllPromotions)
                        .POST("/{id}/file", promotionHandler::handleSaveFileToPromotion)
                        .DELETE("/{id}", promotionHandler::handleDeletePromotion)
                )
                .build();
    }
}
