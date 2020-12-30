package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.PromotionCard;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.exceptions.NotFoundException;
import kz.danke.kids.shop.util.PartImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class PromotionServiceTest extends AbstractServiceLayer {

    @BeforeEach
    public void setup() {
        Mockito.reset(super.promotionRepository);
    }

    @Test
    public void promotionServiceTest_SavePromotion() {
        PromotionCard promotionCard = PromotionCard.builder().build();

        Mockito.when(super.promotionRepository.save(promotionCard)).thenReturn(Mono.just(promotionCard));

        StepVerifier.create(super.promotionService.save(promotionCard))
                .expectSubscription()
                .expectNextMatches(pc -> !StringUtils.isEmpty(pc.getId()))
                .verifyComplete();

        verify(super.promotionRepository, Mockito.times(1)).save(promotionCard);
    }

    @Test
    public void promotionServiceTest_FindAll() {
        PromotionCard promotionCard = PromotionCard.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.promotionRepository.findAll()).thenReturn(Flux.just(promotionCard));

        StepVerifier.create(super.promotionService.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        verify(super.promotionRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void promotionServiceTest_DeleteByPromotionId() {
        String testId = UUID.randomUUID().toString();

        Mockito.when(super.promotionRepository.existsById(testId)).thenReturn(Mono.just(true));
        Mockito.when(super.promotionRepository.deleteById(testId)).thenReturn(Mono.empty());

        StepVerifier.create(super.promotionService.deletePromotionCardById(testId))
                .expectSubscription()
                .verifyComplete();

        verify(super.promotionRepository, Mockito.times(1)).deleteById(testId);
    }

    @Test
    public void promotionServiceTest_DeletePromotionById_ReturnClothNotFoundException() {
        Mockito.when(super.promotionRepository.existsById(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(super.promotionService.deletePromotionCardById(anyString()))
                .expectError(ClothNotFoundException.class)
                .verify();

        verify(super.promotionRepository, times(1)).existsById(anyString());
        verify(super.promotionRepository, times(0)).deleteById(anyString());
    }

    @Test
    public void promotionServiceTest_SaveFileToPromotion() {
        PromotionCard promotionCard = PromotionCard.builder().id(UUID.randomUUID().toString()).build();

        Part part = new PartImpl(promotionCard.getId());

        when(super.promotionRepository.save(promotionCard)).thenReturn(Mono.just(promotionCard));
        when(super.promotionRepository.findById(promotionCard.getId())).thenReturn(Mono.just(promotionCard));

        StepVerifier.create(super.promotionService.saveFileToPromotionCard(part, promotionCard.getId()))
                .expectSubscription()
                .expectNextMatches(pm -> !StringUtils.isEmpty(pm.getImage()))
                .verifyComplete();

        verify(super.promotionRepository, Mockito.times(1)).save(promotionCard);
        verify(super.promotionRepository, Mockito.times(1)).findById(promotionCard.getId());
    }

    @Test
    public void promotionServiceTest_SaveFileToPromotion_ReturnClothNotFoundException() {
        Part part = new PartImpl("test");

        when(super.promotionRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(super.promotionService.saveFileToPromotionCard(part, anyString()))
                .expectError(NotFoundException.class)
                .verify();

        verify(super.promotionRepository, times(1)).findById(anyString());
        verify(super.promotionRepository, times(0)).save(any(PromotionCard.class));
    }
}
