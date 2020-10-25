package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.Material;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.exceptions.FileProcessingException;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ClothServiceImpl implements ClothService {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;

    public ClothServiceImpl(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
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
