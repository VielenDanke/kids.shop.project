package kz.danke.user.service.repository;

import kz.danke.user.service.document.User;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveUserRepository extends ReactiveElasticsearchRepository<User, String> {

    Mono<User> findByUsername(String username);
}
