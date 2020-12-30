package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.util.PartImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ClothServiceTest extends AbstractServiceLayer {

    @BeforeEach
    public void setup() {
        Mockito.reset(super.clothRepository);
    }

    @Test
    public void clothServiceTest_SaveCloth() {
        Cloth cloth = Cloth.builder().build();
        
        Mockito.when(super.clothRepository.save(cloth)).thenReturn(Mono.just(cloth));

        StepVerifier.create(super.clothService.save(cloth))
                .expectSubscription()
                .expectNextMatches(cl -> !StringUtils.isEmpty(cl.getId()))
                .verifyComplete();

        Mockito.verify(super.clothRepository, times(1)).save(cloth);
    }

    @Test
    public void clothServiceTest_DeleteById() {
        String testId = UUID.randomUUID().toString();

        Mockito.when(super.clothRepository.existsById(testId)).thenReturn(Mono.just(true));
        Mockito.when(super.clothRepository.deleteById(testId)).thenReturn(Mono.empty());

        StepVerifier.create(super.clothService.deleteById(testId))
                .expectSubscription()
                .verifyComplete();

        Mockito.verify(super.clothRepository, times(1)).existsById(testId);
        Mockito.verify(super.clothRepository, times(1)).deleteById(testId);
    }

    @Test
    public void clothServiceTest_DeleteById_ReturnExceptionClothNotFound() {
        String test = "test";

        Mockito.when(super.clothRepository.existsById(anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(super.clothService.deleteById(test))
                .expectError(ClothNotFoundException.class)
                .verify();

        Mockito.verify(super.clothRepository, times(1)).existsById(anyString());
        Mockito.verify(super.clothRepository, times(0)).deleteById(anyString());
    }

    @Test
    public void clothServiceTest_FindAllByTestSearching() {
        PublicSearchingObject searchingObject = new PublicSearchingObject();
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.queryCreator.findAllTextSearching(searchingObject, Cloth.class))
                .thenReturn(Flux.just(new SearchHit<>(
                                cloth.getId(),
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

        Mockito.verify(super.queryCreator, times(1))
                .findAllTextSearching(searchingObject, Cloth.class);
    }

    @Test
    public void clothServiceTest_FindAllByIdIn() {
        String testId = UUID.randomUUID().toString();
        Cloth cloth = Cloth.builder().id(testId).build();

        Mockito.when(super.queryCreator.findAllByIdIn(Cloth.class, testId))
                .thenReturn(Flux.just(new SearchHit<>(
                                testId,
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

        Mockito.verify(super.queryCreator, times(1))
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

        Mockito.verify(super.clothRepository, times(1))
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

        Mockito.verify(super.clothRepository, times(1))
                .findById(testId);
    }

    @Test
    public void clothServiceTest_FindById_ReturnExceptionClothNotFound() {
        when(super.clothRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(super.clothService.findById(anyString()))
                .expectError(ClothNotFoundException.class)
                .verify();

        verify(super.clothRepository, times(1)).findById(anyString());
    }

    @Test
    public void clothServiceTest_FindAll() {
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.clothRepository.findAll()).thenReturn(Flux.just(cloth));

        StepVerifier.create(super.clothService.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.clothRepository, times(1)).findAll();
    }

    @Test
    public void clothServiceTest_SaveClothWithFile() {
        String testId = UUID.randomUUID().toString();
        Cloth cloth = Cloth.builder().id(testId).build();

        Mockito.when(super.clothRepository.save(cloth)).thenReturn(Mono.just(cloth));
        Mockito.when(super.clothRepository.findById(testId)).thenReturn(Mono.just(cloth));

        Part part = new PartImpl(testId);

        StepVerifier.create(super.clothService.addFilesToCloth(Collections.singletonList(part), testId))
                .expectSubscription()
                .expectNextMatches(cl -> !(cl.getImages().size() <= 0))
                .verifyComplete();

        Mockito.verify(super.clothRepository, times(1)).save(cloth);
        Mockito.verify(super.clothRepository, times(1)).findById(testId);
    }

    @Test
    public void clothServiceTest_SaveClothWithFile_ReturnClothNotFoundException() {
        when(super.clothRepository.findById(anyString())).thenReturn(Mono.empty());

        Part part = new PartImpl("test");

        StepVerifier.create(super.clothService.addFilesToCloth(Collections.singletonList(part), anyString()))
                .expectError(ClothNotFoundException.class)
                .verify();

        verify(super.clothRepository, times(1)).findById(anyString());
        verify(super.clothRepository, times(0)).save(any(Cloth.class));
    }
}
