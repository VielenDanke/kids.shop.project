package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {

    Mono<Category> save(Category category);

    Flux<Category> findAll();
}
