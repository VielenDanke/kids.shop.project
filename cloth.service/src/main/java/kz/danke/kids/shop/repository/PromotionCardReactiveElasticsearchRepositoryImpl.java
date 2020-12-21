package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.PromotionCard;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionCardReactiveElasticsearchRepositoryImpl extends ReactiveElasticsearchRepository<PromotionCard, String> {
}
