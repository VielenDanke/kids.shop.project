package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

public class CategoryReactiveElasticsearchRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        super.categoryRepository.deleteAll().block();
        super.categoryRepository.save(super.testCategory).block();
    }

    @AfterEach
    public void clean() {
        super.categoryRepository.deleteAll().block();
    }

    @Test
    public void categoryRepositoryTest_SaveCategory() {
        Category category = super.testCategory;

        category.setId(UUID.randomUUID().toString());

        StepVerifier.create(super.categoryRepository.save(category))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void categoryRepositoryTest_FindAll() {
        StepVerifier.create(super.categoryRepository.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }
}
