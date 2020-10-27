package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.ClothDTO;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.dto.response.ClothSaveResponse;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
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
                .onErrorContinue(Exception.class, (ex, obj) -> ServerResponse.badRequest().body(
                        Mono.just(ResponseFailed.builder()
                                .description(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "Something went wrong")
                                .type(ex.toString())
                                .build()),
                        ResponseFailed.class
                )).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> handleFileSaving(ServerRequest serverRequest) {
        final String imageKey = "image";
        final String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.get(imageKey))
                .flatMap(partList -> clothService.addFilesToCloth(partList, id))
                .flatMap(cloth -> ServerResponse.ok().body(Mono.just("Files successfully added"), String.class))
                .onErrorContinue(Exception.class, (ex, obj) -> ServerResponse.badRequest().body(
                        Mono.just(ResponseFailed.builder()
                                .description(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "Something went wrong")
                                .type(ex.toString())
                                .build()),
                        ResponseFailed.class
                        )
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> handleClothTextSearching(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PublicSearchingObject.class)
                .flatMapMany(clothService::findAllTextSearching)
                .map(ClothDTO::toClothDTO)
                .collectList()
                .flatMap(clothDTOS -> ServerResponse.ok().body(Mono.just(clothDTOS), ClothDTO.class))
                .onErrorContinue((throwable, o) ->
                        ServerResponse.badRequest().body(Mono.just(
                                ResponseFailed.builder()
                                .description(throwable.getLocalizedMessage() != null ? throwable.getLocalizedMessage() : "Searching failed")
                                .type(throwable.toString())
                                .build()
                        ), ResponseFailed.class)
                ).switchIfEmpty(ServerResponse.notFound().build());
    }
}
