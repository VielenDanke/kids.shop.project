package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

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
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

        return Flux.fromIterable(files)
                .map(file -> (FilePart) file)
                .map(filePart -> {
                    String filename = filePart.filename();

                    String finalFileName = UUID.randomUUID().toString() + filename;

                    Path pathToFile = Paths.get(properties.getDir().getImageStore() + finalFileName);

                    filePart.transferTo(pathToFile);

                    return finalFileName;
                })
                .doOnEach(stringSignal -> copyOnWriteArrayList.add(stringSignal.get()))
                .then(clothReactiveElasticsearchRepositoryImpl.findById(id))
                .flatMap(cloth -> {
                    cloth.getImages().addAll(copyOnWriteArrayList);
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
