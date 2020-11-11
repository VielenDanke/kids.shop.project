package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Cloth;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface ClothReactiveElasticsearchRepositoryImpl extends ReactiveElasticsearchRepository<Cloth, String> {

    Flux<Cloth> findByIdIn(Collection<String> ids);
}
