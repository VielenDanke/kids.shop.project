package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

public class ClothReactiveElasticsearchRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        super.clothRepository.deleteAll().block();
        super.clothRepository.save(super.testCloth).block();
    }

    @AfterEach
    public void clean() {
        super.clothRepository.deleteAll().block();
    }

    @Test
    public void clothRepositoryTest_SaveNewCloth() {
        Cloth cloth = super.testCloth;

        cloth.setId(UUID.randomUUID().toString());

        StepVerifier.create(super.clothRepository.save(cloth))
                .expectSubscription()
                .expectNextMatches(cl -> cl.getId().equals(super.testCloth.getId()))
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_FindAll() {
        StepVerifier.create(super.clothRepository.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_FindAllByIdIn() {
        StepVerifier.create(super.queryCreator.findAllByIdIn(Cloth.class, super.testCloth.getId()))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_FindAllByIdIn_NoCloth() {
        StepVerifier.create(super.queryCreator.findAllByIdIn(Cloth.class, UUID.randomUUID().toString()))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_FindAllTestSearching_EmptySearchingObject() {
        StepVerifier.create(super.queryCreator.findAllTextSearching(null, Cloth.class))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_FindAllTextSearching_NonEmptySearchingObject() {
        PublicSearchingObject publicSearchingObject = new PublicSearchingObject();

        publicSearchingObject.setCategory(super.testClothCategory);

        StepVerifier.create(super.queryCreator.findAllTextSearching(publicSearchingObject, Cloth.class))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void clothRepositoryTest_DeleteById() {
        super.clothRepository.deleteById(super.testCloth.getId()).block();

        StepVerifier.create(super.clothRepository.findById(super.testCloth.getId()))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }
}
