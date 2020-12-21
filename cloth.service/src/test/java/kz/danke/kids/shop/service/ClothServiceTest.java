package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.UUID;

public class ClothServiceTest extends AbstractServiceLayer {

    @Test
    public void clothServiceTest_SaveCloth() {
        Cloth cloth = Cloth.builder().build();
        
        Mockito.when(super.clothRepository.save(cloth)).thenReturn(Mono.just(cloth));

        StepVerifier.create(super.clothService.save(cloth))
                .expectSubscription()
                .expectNextMatches(cl -> !StringUtils.isEmpty(cl.getId()))
                .verifyComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1)).save(cloth);
    }

    @Test
    public void clothServiceTest_DeleteById() {
        String testId = UUID.randomUUID().toString();

        Mockito.when(super.clothRepository.deleteById(testId)).then(Answers.RETURNS_DEFAULTS);

        StepVerifier.create(super.clothService.deleteById(testId))
                .expectSubscription()
                .expectComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1)).deleteById(testId);
    }

    @Test
    public void clothServiceTest_FindAllByTestSearching() {
        PublicSearchingObject searchingObject = new PublicSearchingObject();
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.queryCreator.findAllTextSearching(searchingObject, Cloth.class))
                .thenReturn(Flux.just(new SearchHit<>(
                                fileName,
                                1.0f,
                                new Object[]{},
                                Collections.emptyMap(),
                                cloth)
                        )
                );

        StepVerifier.create(super.clothService.findAllTextSearching(searchingObject))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.queryCreator, Mockito.times(1))
                .findAllTextSearching(searchingObject, Cloth.class);
    }

    @Test
    public void clothServiceTest_FindAllByIdIn() {
        String testId = UUID.randomUUID().toString();
        Cloth cloth = Cloth.builder().id(testId).build();

        Mockito.when(super.queryCreator.findAllByIdIn(Cloth.class, testId))
                .thenReturn(Flux.just(new SearchHit<>(
                                fileName,
                                1.0f,
                                new Object[]{},
                                Collections.emptyMap(),
                                cloth)
                        )
                );

        StepVerifier.create(super.clothService.findByIdIn(testId))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.queryCreator, Mockito.times(1))
                .findAllByIdIn(Cloth.class, testId);
    }

    @Test
    public void clothServiceTest_SaveWithoutSetId() {
        Cloth cloth = Cloth.builder().build();

        Mockito.when(super.clothRepository.save(cloth)).thenReturn(Mono.just(cloth));

        StepVerifier.create(super.clothService.saveWithoutSetId(cloth))
                .expectSubscription()
                .expectNextMatches(cl -> StringUtils.isEmpty(cl.getId()))
                .verifyComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1))
                .save(cloth);
    }

    @Test
    public void clothServiceTest_FindById() {
        String testId = UUID.randomUUID().toString();
        Cloth cloth = Cloth.builder().id(testId).build();

        Mockito.when(super.clothRepository.findById(testId)).thenReturn(Mono.just(cloth));

        StepVerifier.create(super.clothService.findById(testId))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1))
                .findById(testId);
    }

    @Test
    public void clothServiceTest_FindAll() {
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.clothRepository.findAll()).thenReturn(Flux.just(cloth));

        StepVerifier.create(super.clothService.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void clothServiceTest_SaveClothWithFile() {
        String testId = UUID.randomUUID().toString();
        Cloth cloth = Cloth.builder().id(testId).build();

        Mockito.when(super.clothRepository.save(cloth)).thenReturn(Mono.just(cloth));
        Mockito.when(super.clothRepository.findById(testId)).thenReturn(Mono.just(cloth));

        Part part = new PartImpl();

        StepVerifier.create(super.clothService.addFilesToCloth(Collections.singletonList(part), testId))
                .expectSubscription()
                .expectNextMatches(cl -> !(cl.getImages().size() <= 0))
                .verifyComplete();

        Mockito.verify(super.clothRepository, Mockito.times(1)).save(cloth);
        Mockito.verify(super.clothRepository, Mockito.times(1)).findById(testId);
    }
}
