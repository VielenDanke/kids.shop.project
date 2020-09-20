package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClothService {

    Flux<Cloth> findAll();

    Mono<Cloth> save(ClothSaveRequest clothSaveRequest);
}
