package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Cloth;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothReactiveElasticsearchRepositoryImpl extends ReactiveElasticsearchRepository<Cloth, String> {
}
