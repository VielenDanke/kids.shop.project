package kz.danke.user.service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

public class UserRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        Mono.just("cloth.shop.user")
                .filter(index -> {
                    Optional<Boolean> existOptional = reactiveElasticsearchClient.indices().existsIndex(getIndexRequest ->
                            getIndexRequest.indices(index)).blockOptional();
                    return existOptional.map(aBoolean -> !aBoolean).orElse(true);
                })
                .flatMap(index -> reactiveElasticsearchClient.indices().createIndex(createIndexRequest ->
                        createIndexRequest.index(index)))
                .block();
        userRepository.deleteAll().block();
        userRepository.save(testUser).block();
    }

    @Test
    public void userRepository_SaveNewUser() {
        userRepository.deleteAll().block();

        StepVerifier.create(userRepository.save(testUser))
                .expectSubscription()
                .expectNextMatches(u -> u.getId().equals(testUser.getId()))
                .verifyComplete();
    }

    @Test
    public void userRepository_FindUserByUsername() {
        StepVerifier.create(userRepository.findByUsername(testData))
                .expectSubscription()
                .expectNextMatches(u -> u.getUsername().equals(testData))
                .verifyComplete();
    }

    @Test
    public void userRepository_FindUserByUsername_NotFound() {
        StepVerifier.create(userRepository.findByUsername("invalid"))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }
}
