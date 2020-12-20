package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Cloth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class ClothReactiveElasticsearchRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        super.clothRepository.deleteAll().block();
    }

    @AfterEach
    public void clean() {
        super.clothRepository.deleteAll().block();
    }

    @Test
    public void clothTest_SaveNewCloth() {
        StepVerifier.create(super.clothRepository.save(super.cloth))
                .expectSubscription()
                .expectNextMatches(cl -> cl.getId().equals(super.cloth.getId()))
                .verifyComplete();
    }

    @Test
    public void clothTest_FindAll() {
        StepVerifier.create(super.clothRepository.findAll())
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    public void clothTest_FindAllByIdIn() {
        StepVerifier.create(super.queryCreator.findAllByIdIn(Cloth.class, super.cloth.getId()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    public void clothTest_FindAllTestSearching() {
        StepVerifier.create(super.queryCreator.findAllTextSearching(null, Cloth.class))
                .expectSubscription()
                .verifyComplete();
    }
}
