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
                .route().path("/cart", builder -> builder
                        .POST("/reserve", userHandler::handleCartProcess)
                        .POST("/reserve/decline", userHandler::handleCartReserveDecline)
                        .POST("/retrieve", userHandler::handleCartRetrieve)
                        .POST("/process", userHandler::handleChargeProcess))
                .path("/cabinet", builder -> builder
                        .GET("", userHandler::getUserCabinet)
                        .POST("/update", userHandler::updateUser))
                .path("/auth", builder -> builder
                        .POST("/registration", userHandler::saveNewUser))
                .build();
    }
}
