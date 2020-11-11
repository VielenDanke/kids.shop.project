package kz.danke.kids.shop.service.searching;

import org.springframework.data.elasticsearch.core.SearchHit;
import reactor.core.publisher.Flux;

public interface QueryCreator<T, S> {

    Flux<SearchHit<T>> findAllByIdIn(Class<T> tClass, String... ids);

    Flux<SearchHit<T>> findAllTextSearching(S s, Class<T> tClass);
}
