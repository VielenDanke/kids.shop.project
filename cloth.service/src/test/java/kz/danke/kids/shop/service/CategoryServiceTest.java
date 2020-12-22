package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

public class CategoryServiceTest extends AbstractServiceLayer {

    @BeforeEach
    public void setup() {
        Mockito.reset(super.categoryRepository);
    }

    @Test
    public void categoryServiceTest_SaveCategory() {
        Category category = Category.builder().build();

        Mockito.when(super.categoryRepository.save(category)).thenReturn(Mono.just(category));

        StepVerifier.create(super.categoryService.save(category))
                .expectSubscription()
                .expectNextMatches(cat -> !StringUtils.isEmpty(cat.getId()))
                .verifyComplete();

        Mockito.verify(super.categoryRepository, Mockito.times(1)).save(category);
    }

    @Test
    public void categoryServiceTest_FindAll() {
        Category category = Category.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.categoryRepository.findAll()).thenReturn(Flux.just(category));

        StepVerifier.create(super.categoryService.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.categoryRepository, Mockito.times(1)).findAll();
    }
}
