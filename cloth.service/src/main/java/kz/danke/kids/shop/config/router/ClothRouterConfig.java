package kz.danke.kids.shop.config.router;

import kz.danke.kids.shop.config.handler.ClothHandler;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ClothRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> clothRouterFunction(
            ClothService clothService, ClothHandler clothHandler
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
        );
    }
}
