package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Cloth;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface ClothReactiveElasticsearchRepository extends ReactiveElasticsearchRepository<Cloth, String> {
}
