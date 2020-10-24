package kz.danke.kids.shop.config.router;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ClothRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> clothRouterFunction(ClothService clothService) {
        return RouterFunctions.route(
                RequestPredicates.GET("/clothes"),
                serverRequest -> ServerResponse.ok().body(clothService.findAll(), Cloth.class)
        ).andRoute(
                RequestPredicates.GET("/clothes/{id}"),
                serverRequest -> ServerResponse.ok().body(
                        clothService.findById(serverRequest.pathVariable("id")), Cloth.class
                )
        );
    }
}
