package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClothServiceImpl implements ClothService {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;
    private final QueryCreator<Cloth, PublicSearchingObject> clothTextSearching;
    private final AppConfigProperties properties;

    public ClothServiceImpl(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl,
                            QueryCreator<Cloth, PublicSearchingObject> clothTextSearching,
                            AppConfigProperties properties) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
        this.clothTextSearching = clothTextSearching;
        this.properties = properties;
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return clothReactiveElasticsearchRepositoryImpl.deleteById(id);
    }

    @Override
    public Flux<Cloth> findByIdIn(String... ids) {
        return clothTextSearching.findAllByIdIn(Cloth.class, ids).map(SearchHit::getContent);
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
    public Mono<Cloth> saveWithoutSetId(Cloth cloth) {
        return clothReactiveElasticsearchRepositoryImpl.save(cloth);
    }

    @Override
    public Mono<Cloth> findById(String id) {
        return clothReactiveElasticsearchRepositoryImpl
                .findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(
                        new ClothNotFoundException(String.format("Cloth with ID %s not found", id))))
                );
    }

    @Override
    public Mono<Cloth> addFilesToCloth(List<Part> files, final String id) {
        Mono<Cloth> clothById = clothReactiveElasticsearchRepositoryImpl.findById(id);

        return Flux.fromIterable(files)
                .map(part -> {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    DataBufferUtils.write(part.content(), outputStream)
                            .subscribe(DataBufferUtils.releaseConsumer());

                    return outputStream.toByteArray();
                })
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .collectList()
                .zipWith(clothById)
                .flatMap(tuple -> {
                    Cloth cloth = tuple.getT2();
                    List<String> images = tuple.getT1();

                    cloth.getImages().addAll(images);

                    return clothReactiveElasticsearchRepositoryImpl.save(cloth);
                })
                .onErrorContinue(Exception.class, (ex, obj) -> {
                    log.error("Exception during file saving occurred", ex);
                });


    }

    @Override
    public Flux<Cloth> findAll() {
        return clothReactiveElasticsearchRepositoryImpl.findAll();
    }
}
