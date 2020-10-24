package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClothServiceImpl implements ClothService {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;

    public ClothServiceImpl(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
    }

    @Override
    public Mono<Cloth> save(ClothSaveRequest clothSaveRequest) {
        Cloth cloth = Cloth.builder()
                .description(clothSaveRequest.getDescription())
                .build();

        return clothReactiveElasticsearchRepositoryImpl
                .save(cloth);
    }

    @Override
    public Mono<Cloth> findById(String id) {
        return clothReactiveElasticsearchRepositoryImpl
                .findById(id);
    }

    @Override
    public Flux<Cloth> findAll() {
        return clothReactiveElasticsearchRepositoryImpl.findAll();
    }
}
