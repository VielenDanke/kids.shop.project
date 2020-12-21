package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.PromotionCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

public class PromotionServiceTest extends AbstractServiceLayer {

    @Test
    public void promotionServiceTest_SavePromotion() {
        PromotionCard promotionCard = PromotionCard.builder().build();

        Mockito.when(super.promotionRepository.save(promotionCard)).thenReturn(Mono.just(promotionCard));

        StepVerifier.create(super.promotionService.save(promotionCard))
                .expectSubscription()
                .expectNextMatches(pc -> !StringUtils.isEmpty(pc.getId()))
                .verifyComplete();

        Mockito.verify(super.promotionRepository, Mockito.times(1)).save(promotionCard);
    }

    @Test
    public void promotionServiceTest_FindAll() {
        PromotionCard promotionCard = PromotionCard.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(super.promotionRepository.findAll()).thenReturn(Flux.just(promotionCard));

        StepVerifier.create(super.promotionService.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(super.promotionRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void promotionServiceTest_DeleteByPromotionId() {
        String testId = UUID.randomUUID().toString();

        Mockito.when(super.promotionRepository.deleteById(testId)).then(Answers.RETURNS_DEFAULTS);

        StepVerifier.create(super.promotionService.deletePromotionCardById(testId))
                .expectSubscription()
                .verifyComplete();

        Mockito.verify(super.promotionRepository, Mockito.times(1)).deleteById(testId);
    }

    @Test
    public void promotionServiceTest_SaveFileToPromotion() {
        PromotionCard promotionCard = PromotionCard.builder().id(UUID.randomUUID().toString()).build();

        Part part = new PartImpl();

        Mockito.when(super.promotionRepository.save(promotionCard)).thenReturn(Mono.just(promotionCard));
        Mockito.when(super.promotionRepository.findById(promotionCard.getId())).thenReturn(Mono.just(promotionCard));

        StepVerifier.create(super.promotionService.saveFileToPromotionCard(part, promotionCard.getId()))
                .expectSubscription()
                .expectNextMatches(pm -> !StringUtils.isEmpty(pm.getImage()))
                .verifyComplete();

        Mockito.verify(super.promotionRepository, Mockito.times(1)).save(promotionCard);
        Mockito.verify(super.promotionRepository, Mockito.times(1)).findById(promotionCard.getId());
    }
}
