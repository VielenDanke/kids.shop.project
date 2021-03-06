package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.PromotionCard;
import kz.danke.kids.shop.dto.request.PromotionCardSaveRequest;
import kz.danke.kids.shop.exceptions.EmptyRequestException;
import kz.danke.kids.shop.exceptions.NotFoundException;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public Mono<ServerResponse> handleDeletePromotion(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable("id"))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmptyRequestException("Path variable is not exists"))))
                .flatMap(promotionService::deletePromotionCardById)
                .then(ServerResponse.ok().build())
                .onErrorResume(Exception.class, ex -> ServerResponse.status(500).body(
                        Mono.just(
                                new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                                ResponseFailed.class
                        ));
    }

    public Mono<ServerResponse> handleSavePromotion(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PromotionCardSaveRequest.class)
                .map(promotionCardSaveRequest -> PromotionCard.builder()
                        .name(promotionCardSaveRequest.getName())
                        .description(promotionCardSaveRequest.getDescription())
                        .build()
                )
                .flatMap(promotionService::save)
                .flatMap(card -> ServerResponse.status(HttpStatus.CREATED).body(Mono.just(card), PromotionCard.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                ));
    }

    public Mono<ServerResponse> getAllPromotions(ServerRequest serverRequest) {
        return promotionService.findAll()
                .collectList()
                .flatMap(cardList -> ServerResponse.ok().body(Mono.just(cardList), PromotionCard.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                ));
    }

    public Mono<ServerResponse> handleSaveFileToPromotion(ServerRequest serverRequest) {
        final String imageKey = "image";
        final String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.getFirst(imageKey))
                .flatMap(part -> promotionService.saveFileToPromotionCard(part, id))
                .flatMap(promCard -> ServerResponse.ok().body(Mono.just(promCard), PromotionCard.class))
                .onErrorResume(NotFoundException.class, ex -> ServerResponse.status(HttpStatus.NOT_FOUND).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                        )
                )
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                        )
                );
    }
}
