package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.PromotionCard;
import kz.danke.kids.shop.dto.request.PromotionCardSaveRequest;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PromotionHandler {

    private final PromotionService promotionService;

    @Autowired
    public PromotionHandler(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    public Mono<ServerResponse> handleSavePromotion(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PromotionCardSaveRequest.class)
                .map(promotionCardSaveRequest -> PromotionCard.builder()
                        .name(promotionCardSaveRequest.getName())
                        .description(promotionCardSaveRequest.getDescription())
                        .build()
                )
                .flatMap(promotionService::save)
                .flatMap(card -> ServerResponse.ok().body(Mono.just(card), PromotionCard.class));
    }

    public Mono<ServerResponse> getAllPromotions(ServerRequest serverRequest) {
        return promotionService.findAll()
                .collectList()
                .flatMap(cardList -> ServerResponse.ok().body(Mono.just(cardList), PromotionCard.class));
    }

    public Mono<ServerResponse> handleSaveFileToPromotion(ServerRequest serverRequest) {
        final String imageKey = "image";
        final String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.getFirst(imageKey))
                .flatMap(part -> promotionService.saveFileToPromotionCard(part, id))
                .flatMap(part -> ServerResponse.ok().body(Mono.just("Files successfully added"), String.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.badRequest().body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                        )
                );
    }
}
