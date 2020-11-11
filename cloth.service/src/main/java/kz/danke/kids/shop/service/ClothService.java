package kz.danke.kids.shop.service;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface ClothService {

    Flux<Cloth> findAll();

    Mono<Cloth> save(Cloth cloth);

    Mono<Cloth> saveWithoutSetId(Cloth cloth);

    Mono<Cloth> findById(String id);

    Mono<Cloth> addFilesToCloth(List<Part> files, String id);

    Flux<Cloth> findAllTextSearching(PublicSearchingObject searchingObject);

    Flux<Cloth> findByIdIn(String... ids);
}
