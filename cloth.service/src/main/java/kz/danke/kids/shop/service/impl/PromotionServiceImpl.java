package kz.danke.kids.shop.service.impl;

import kz.danke.kids.shop.document.PromotionCard;
import kz.danke.kids.shop.repository.PromotionCartReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.PromotionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionCartReactiveElasticsearchRepositoryImpl promotionRepository;

    @Autowired
    public PromotionServiceImpl(PromotionCartReactiveElasticsearchRepositoryImpl promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Mono<PromotionCard> save(PromotionCard promotionCard) {
        return Mono.just(promotionCard)
                .doOnNext(prCard -> prCard.setId(UUID.randomUUID().toString()))
                .flatMap(promotionRepository::save);
    }

    @Override
    public Flux<PromotionCard> findAll() {
        return promotionRepository.findAll();
    }

    @Override
    public Mono<Void> deletePromotionCardById(String id) {
        return Mono.just(id)
                .flatMap(promotionRepository::deleteById);
    }

    @Override
    public Mono<PromotionCard> saveFileToPromotionCard(Part part, String id) {
        Mono<PromotionCard> promotionCardById = promotionRepository.findById(id);

        return Mono.just(part)
                .map(file -> {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    DataBufferUtils.write(file.content(), outputStream)
                            .subscribe(DataBufferUtils.releaseConsumer());

                    return outputStream.toByteArray();
                })
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .zipWith(promotionCardById)
                .flatMap(tuple -> {
                    PromotionCard card = tuple.getT2();
                    String image = tuple.getT1();

                    card.setImage(image);

                    return promotionRepository.save(card);
                })
                .onErrorContinue(Exception.class, (ex, obj) -> {
                    log.error("Exception during file saving occurred", ex);
                });
    }
}
