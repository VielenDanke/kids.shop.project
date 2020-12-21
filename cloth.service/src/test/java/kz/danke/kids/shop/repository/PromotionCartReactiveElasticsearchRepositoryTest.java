package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.PromotionCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

public class PromotionCartReactiveElasticsearchRepositoryTest extends AbstractRepositoryLayer {

    @BeforeEach
    public void setup() {
        super.promotionRepository.deleteAll().block();
        super.promotionRepository.save(super.testPromotion).block();
    }

    @AfterEach
    public void clean() {
        super.promotionRepository.deleteAll().block();
    }

    @Test
    public void promotionRepositoryTest_SavePromotion() {
        PromotionCard promotionCard = super.testPromotion;

        promotionCard.setId(UUID.randomUUID().toString());

        StepVerifier.create(super.promotionRepository.save(promotionCard))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void promotionRepositoryTest_FindAll() {
        StepVerifier.create(super.promotionRepository.findAll())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void promotionRepositoryTest_DeleteById() {
        String promotionTestId = super.testPromotion.getId();

        super.promotionRepository.deleteById(promotionTestId).block();

        StepVerifier.create(super.promotionRepository.findById(promotionTestId))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }
}
