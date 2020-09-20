package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepository;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClothServiceImpl implements ClothService {

    private final ClothReactiveElasticsearchRepository clothReactiveElasticsearchRepository;

    public ClothServiceImpl(ClothReactiveElasticsearchRepository clothReactiveElasticsearchRepository) {
        this.clothReactiveElasticsearchRepository = clothReactiveElasticsearchRepository;
    }

    @Override
    public Mono<Cloth> save(ClothSaveRequest clothSaveRequest) {
        Cloth cloth = Cloth.builder()
                .description(clothSaveRequest.getDescription())
                .build();

        return clothReactiveElasticsearchRepository
                .save(cloth);
    }

    @Override
    public Flux<Cloth> findAll() {
        return clothReactiveElasticsearchRepository.findAll();
    }
}
