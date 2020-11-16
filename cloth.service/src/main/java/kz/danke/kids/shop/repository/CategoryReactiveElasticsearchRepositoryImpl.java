package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Category;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryReactiveElasticsearchRepositoryImpl extends ReactiveElasticsearchRepository<Category, String> {
}
