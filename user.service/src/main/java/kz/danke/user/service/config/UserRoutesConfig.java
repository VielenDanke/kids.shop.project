package kz.danke.user.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return RouterFunctions
                .route(
                        RequestPredicates.POST("/cart/reserve"),
                        userHandler::handleCartProcess
                )
                .andRoute(
                        RequestPredicates.POST("/cart/process"),
                        userHandler::handleChargeProcess
                )
                .andRoute(
                        RequestPredicates.GET("/cabinet"),
                        userHandler::getUserCabinet
                )
                .andRoute(
                        RequestPredicates.POST("/auth/registration"),
                        userHandler::saveNewUser
                );
    }
}
