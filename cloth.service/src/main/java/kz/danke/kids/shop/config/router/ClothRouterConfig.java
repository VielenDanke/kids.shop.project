package kz.danke.kids.shop.config.router;

import kz.danke.kids.shop.config.handler.CategoryHandler;
import kz.danke.kids.shop.config.handler.ClothHandler;
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
            ClothHandler clothHandler, CategoryHandler categoryHandler
    ) {
        return RouterFunctions.route(
                RequestPredicates.GET("/clothes"),
                clothHandler::handleMainPageClothes
        ).andRoute(
                RequestPredicates.GET("/clothes/{id}"),
                clothHandler::handleClothById
        ).andRoute(
                RequestPredicates.POST("/clothes"),
                clothHandler::handleClothSaving
        ).andRoute(
                RequestPredicates.POST("/clothes/{id}/files"),
                clothHandler::handleFileSaving
        ).andRoute(
                RequestPredicates.POST("/clothes/searching"),
                clothHandler::handleClothTextSearching
        ).andRoute(
                RequestPredicates.POST("/clothes/validate"),
                clothHandler::checkIfAmountEnough
        ).andRoute(
                RequestPredicates.POST("/categories"),
                categoryHandler::addCategory
        ).andRoute(
                RequestPredicates.GET("/categories"),
                categoryHandler::finAllCategories
        );
    }
}
