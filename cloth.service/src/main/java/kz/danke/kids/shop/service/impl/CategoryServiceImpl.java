package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Category;
import kz.danke.kids.shop.repository.CategoryReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryReactiveElasticsearchRepositoryImpl categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryReactiveElasticsearchRepositoryImpl categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Mono<Category> save(Category category) {
        return Mono.just(category)
                .doOnNext(cat -> cat.setId(UUID.randomUUID().toString()))
                .flatMap(categoryRepository::save);
    }

    @Override
    public Flux<Category> findAll() {
        return categoryRepository.findAll();
    }
}
