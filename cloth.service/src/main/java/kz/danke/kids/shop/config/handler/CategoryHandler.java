package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.Category;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CategoryHandler {

    private final CategoryService categoryService;

    @Autowired
    public CategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Mono<ServerResponse> finAllCategories(ServerRequest serverRequest) {
        return categoryService
                .findAll()
                .collectList()
                .flatMap(categories -> ServerResponse.ok().body(Mono.just(categories), Category.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(500).body(
                        Mono.just(
                                new ResponseFailed(
                                        ex.getLocalizedMessage(),
                                        ex.toString(),
                                        serverRequest.path()
                                )
                        ), ResponseFailed.class
                ));
    }

    public Mono<ServerResponse> addCategory(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Category.class)
                .flatMap(categoryService::save)
                .flatMap(category -> ServerResponse.ok().body(Mono.just(category), Category.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(500).body(
                        Mono.just(
                                new ResponseFailed(
                                        ex.getLocalizedMessage(),
                                        ex.toString(),
                                        serverRequest.path()
                                )
                        ), ResponseFailed.class
                ));
    }
}
