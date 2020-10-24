package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClothService {

    Flux<Cloth> findAll();

    Mono<Cloth> save(ClothSaveRequest clothSaveRequest);

    Mono<Cloth> findById(String id);
}
