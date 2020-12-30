package kz.danke.kids.shop.config.handler;

import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.dto.ClothDTO;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.dto.response.ClothSaveResponse;
import kz.danke.kids.shop.exceptions.*;
import kz.danke.kids.shop.service.CategoryService;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;

@Component
@Slf4j
public class ClothHandler {

    private final ClothService clothService;
    private final CategoryService categoryService;

    @Autowired
    public ClothHandler(ClothService clothService, CategoryService categoryService) {
        this.clothService = clothService;
        this.categoryService = categoryService;
    }

    public Mono<ServerResponse> handleClothSaving(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ClothSaveRequest.class)
                .map(clothRequest -> Cloth.builder()
                        .name(clothRequest.getName())
                        .description(clothRequest.getDescription())
                        .materials(clothRequest.getMaterialList())
                        .gender(clothRequest.getSex())
                        .category(clothRequest.getCategory())
                        .price(clothRequest.getPrice())
                        .lineSizes(clothRequest.getLineSizes())
                        .color(clothRequest.getColor())
                        .build()
                )
                .flatMap(clothService::save)
                .map(cloth -> ClothSaveResponse.builder()
                        .id(cloth.getId())
                        .name(cloth.getName())
                        .description(cloth.getDescription())
                        .build()
                ).flatMap(clothSaveResponse -> ServerResponse.status(HttpStatus.CREATED).body(Mono.just(clothSaveResponse), ClothSaveResponse.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.badRequest().body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class)
                );
    }

    public Mono<ServerResponse> handleFileSaving(ServerRequest serverRequest) {
        final String imageKey = "image";
        final String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
                .map(stringPartMultiValueMap -> stringPartMultiValueMap.get(imageKey))
                .flatMap(partList -> clothService.addFilesToCloth(partList, id))
                .map(ClothDTO::toClothDTO)
                .flatMap(clothDto -> ServerResponse.ok().body(Mono.just(clothDto), ClothDTO.class))
                .onErrorResume(ClothNotFoundException.class, ex -> ServerResponse.status(HttpStatus.NOT_FOUND).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                ))
                .onErrorResume(Exception.class, ex -> ServerResponse.badRequest().body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
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
                                new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                        ), ResponseFailed.class)
                );
    }

    public Mono<ServerResponse> processDeclineOrReserve(ServerRequest serverRequest) {
        String path = serverRequest.path();

        boolean isDecline = path.contains("decline");

        return serverRequest.bodyToMono(Cart.class)
                .map(Cart::getClothCartList)
                .flatMap(clothCartList -> {
                    String[] ids = clothCartList.stream().map(ClothCart::getId).toArray(String[]::new);

                    Mono<List<String>> idsList = clothService.findByIdIn(ids)
                            .map(cloth -> {
                                for (ClothCart cr : clothCartList) {
                                    if (cr.getId().equals(cloth.getId())) {
                                        List<LineSize> lineSizes = cloth.getLineSizes();

                                        LineSize lineSize = new LineSize(cr.getAge(), cr.getHeight());

                                        if (!lineSizes.contains(lineSize)) {
                                            return null;
                                        }
                                        int i = lineSizes.indexOf(lineSize);
                                        LineSize lineSizeFromCloth = lineSizes.get(i);
                                        if (isDecline) {
                                            lineSizeFromCloth.setAmount(lineSizeFromCloth.getAmount() + cr.getAmount());
                                        } else {
                                            if (lineSizeFromCloth.getAmount() - cr.getAmount() < 0) {
                                                return null;
                                            }
                                            lineSizeFromCloth.setAmount(lineSizeFromCloth.getAmount() - cr.getAmount());
                                        }
                                        lineSizes.removeIf(ls -> ls.equals(lineSizeFromCloth));
                                        lineSizes.add(lineSizeFromCloth);
                                        cloth.setLineSizes(lineSizes);
                                    }
                                }
                                return cloth;
                            })
                            .filter(Objects::nonNull)
                            .flatMap(clothService::saveWithoutSetId)
                            .switchIfEmpty(Mono.defer(() -> Mono.error(new ClothNotEnoughAmountException("Not enough cloth, cart empty"))))
                            .map(Cloth::getId)
                            .collectList();

                    return Flux.fromIterable(clothCartList)
                            .zipWith(idsList)
                            .filter(tuple -> {
                                List<String> clothIds = tuple.getT2();
                                ClothCart clothCart = tuple.getT1();

                                return clothIds.contains(clothCart.getId());
                            })
                            .map(Tuple2::getT1)
                            .collectList()
                            .map(Cart::new)
                            .flatMap(cart -> ServerResponse.ok().body(Mono.just(cart), Cart.class))
                            .onErrorResume(ClothNotEnoughAmountException.class, ex ->
                                    ServerResponse.status(404).body(
                                            Mono.just(new ResponseFailed(
                                                    ex.getLocalizedMessage(),
                                                    ex.toString(),
                                                    serverRequest.path()
                                            )), ResponseFailed.class
                                    )
                            );
                });
    }

    public Mono<ServerResponse> handleMainPageClothes(ServerRequest serverRequest) {
        return clothService
                .findAll()
                .map(ClothDTO::toClothDTO)
                .collectList()
                .flatMap(clothDTOS -> ServerResponse.ok().body(Mono.just(clothDTOS), ClothDTO.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(500).body(
                        Mono.just(
                                new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                        ), ResponseFailed.class
                ));

    }

    public Mono<ServerResponse> handleClothById(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable("id"))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmptyPathVariableException("Path variable ID is empty"))))
                .flatMap(clothService::findById)
                .flatMap(cloth -> ServerResponse.ok().body(Mono.just(cloth), Cloth.class))
                .onErrorResume(ClothNotFoundException.class, ex -> ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                Mono.just(
                                        new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                                ),
                                ResponseFailed.class
                        )
                )
                .onErrorResume(Exception.class, ex -> ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                Mono.just(
                                        new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                                ),
                                ResponseFailed.class
                        ));
    }

    public Mono<ServerResponse> deleteClothById(ServerRequest serverRequest) {
        return Mono.justOrEmpty(serverRequest.pathVariable("id"))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new EmptyPathVariableException("Path variable ID not found"))))
                .flatMap(clothService::deleteById)
                .then(ServerResponse.ok().build())
                .onErrorResume(EmptyPathVariableException.class, ex -> ServerResponse.badRequest().body(Mono.just(
                        new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                ), ResponseFailed.class))
                .onErrorResume(ClothNotFoundException.class, ex -> ServerResponse.status(HttpStatus.NOT_FOUND).body(Mono.just(
                        new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                ), ResponseFailed.class));
    }

    public Mono<ServerResponse> handleClothCart(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(new ParameterizedTypeReference<List<String>>() {
        })
                .map(list -> list.toArray(String[]::new))
                .flatMapMany(clothService::findByIdIn)
                .collectList()
                .flatMap(list -> ServerResponse.ok().body(Mono.just(list), Cloth.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(
                        new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())
                ), ResponseFailed.class));
    }
}
