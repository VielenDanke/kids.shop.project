package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.Cart;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.ClothCart;
import kz.danke.kids.shop.document.LineSize;
import kz.danke.kids.shop.dto.ClothDTO;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.dto.response.ClothSaveResponse;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class ClothHandler {

    private final ClothService clothService;

    @Autowired
    public ClothHandler(ClothService clothService) {
        this.clothService = clothService;
    }

    public Mono<ServerResponse> handleClothSaving(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ClothSaveRequest.class)
                .map(clothRequest -> Cloth.builder()
                        .name(clothRequest.getName())
                        .description(clothRequest.getDescription())
                        .materials(clothRequest.getMaterialList())
                        .build()
                )
                .flatMap(clothService::save)
                .map(cloth -> ClothSaveResponse.builder()
                        .id(cloth.getId())
                        .name(cloth.getName())
                        .description(cloth.getDescription())
                        .build()
                ).flatMap(clothSaveResponse -> ServerResponse.ok().body(Mono.just(clothSaveResponse), ClothSaveResponse.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.badRequest().body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString())),
                        ResponseFailed.class)
                );
    }

    public Mono<ServerResponse> handleFileSaving(ServerRequest serverRequest) {
        final String imageKey = "image";
        final String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.get(imageKey))
                .flatMap(partList -> clothService.addFilesToCloth(partList, id))
                .flatMap(cloth -> ServerResponse.ok().body(Mono.just("Files successfully added"), String.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.badRequest().body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString())),
                        ResponseFailed.class
                        )
                );
    }

    public Mono<ServerResponse> handleClothTextSearching(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PublicSearchingObject.class)
                .flatMapMany(clothService::findAllTextSearching)
                .map(ClothDTO::toClothDTO)
                .collectList()
                .flatMap(clothDTOS -> ServerResponse.ok().body(Mono.just(clothDTOS), ClothDTO.class))
                .onErrorResume(Exception.class, ex ->
                        ServerResponse.badRequest().body(Mono.just(
                                new ResponseFailed(ex.getLocalizedMessage(), ex.toString())
                        ), ResponseFailed.class)
                );
    }

    public Mono<ServerResponse> checkIfAmountEnough(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Cart.class)
                .map(Cart::getClothCartList)
                .flatMapIterable(clothCarts -> clothCarts)
                .map(ClothCart::getId)
                .flatMap(clothService::findById)
                .map(cloth -> {
                    LineSize lineSize = cloth.getLineSizes().get(0);
                    return new ClothCart(
                            cloth.getId(),
                            lineSize.getAge(),
                            lineSize.getHeight(),
                            lineSize.getColor(),
                            lineSize.getAmount()
                    );
                })
                .collectList()
                .map(Cart::new)
                .flatMap(cart -> ServerResponse.ok().body(Mono.just(cart), Cart.class));
    }
}
