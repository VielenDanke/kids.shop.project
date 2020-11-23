package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.PromotionCard;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionService {

    Mono<PromotionCard> save(PromotionCard promotionCard);

    Flux<PromotionCard> findAll();

    Mono<PromotionCard> saveFileToPromotionCard(Part part, String id);

    Mono<Void> deletePromotionCardById(String id);
}
