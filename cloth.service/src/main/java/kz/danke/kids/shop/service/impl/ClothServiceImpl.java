package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.ClothDTO;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClothServiceImpl implements ClothService {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;
    private final QueryCreator<Cloth, PublicSearchingObject> clothTextSearching;

    public ClothServiceImpl(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl,
                            QueryCreator<Cloth, PublicSearchingObject> clothTextSearching) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
        this.clothTextSearching = clothTextSearching;
    }

    @Override
    public Flux<Cloth> findAllTextSearching(PublicSearchingObject searchingObject) {
        return clothTextSearching
                .findAllTextSearching(searchingObject, Cloth.class)
                .map(SearchHit::getContent);
    }

    @Override
    public Mono<Cloth> save(Cloth cloth) {
        return Mono.just(cloth)
                .doOnNext(cl -> cl.setId(UUID.randomUUID().toString()))
                .flatMap(clothReactiveElasticsearchRepositoryImpl::save);
    }

    @Override
    public Mono<Cloth> findById(String id) {
        return clothReactiveElasticsearchRepositoryImpl
                .findById(id);
    }

    @Override
    public Mono<Cloth> addFilesToCloth(List<Part> files, String id) {
        return Flux.fromIterable(files)
                .flatMap(Part::content)
                .flatMap(content -> {
                    try {
                        return Mono.just(content.asInputStream().readAllBytes());
                    } catch (IOException e) {
                        log.error("Error processing files");
                        return Mono.error(e);
                    }
                })
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .collectList()
                .flatMap(fileList -> clothReactiveElasticsearchRepositoryImpl.findById(id)
                        .doOnNext(cloth -> cloth.getImages().addAll(fileList))
                        .flatMap(clothReactiveElasticsearchRepositoryImpl::save)
                );
    }

    @Override
    public Flux<Cloth> findAll() {
        return clothReactiveElasticsearchRepositoryImpl.findAll();
    }
}
