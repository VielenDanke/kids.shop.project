package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Category;
import kz.danke.kids.shop.repository.CategoryReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryReactiveElasticsearchRepositoryImpl categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryReactiveElasticsearchRepositoryImpl categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Mono<Category> save(Category category) {
        return categoryRepository.save(category);
    }
}
