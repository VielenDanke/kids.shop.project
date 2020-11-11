package kz.danke.kids.shop.service.searching;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ClothSearchingQueryCreator implements QueryCreator<Cloth, PublicSearchingObject> {

    private final ReactiveElasticsearchOperations elasticsearchOperations;
    private final ClothReactiveElasticsearchRepositoryImpl clothRepository;

    @Autowired
    public ClothSearchingQueryCreator(ReactiveElasticsearchOperations elasticsearchOperations,
                                      ClothReactiveElasticsearchRepositoryImpl clothRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.clothRepository = clothRepository;
    }

    @Override
    public Flux<SearchHit<Cloth>> findAllByIdIn(Class<Cloth> clothClass, String... ids) {
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(QueryBuilders.idsQuery().addIds(ids)).build();

        return elasticsearchOperations.search(build, Cloth.class);
    }

    @Override
    public Flux<SearchHit<Cloth>> findAllTextSearching(PublicSearchingObject searchingObject, Class<Cloth> cClass) {
        if (searchingObject == null) {
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().build();
            return elasticsearchOperations.search(nativeSearchQuery, cClass);
        }
        long countNonNullFields = Stream.of(searchingObject.getName(), searchingObject.getDescription(), searchingObject.getMaterial(),
                searchingObject.getColor(), searchingObject.getHeight(), searchingObject.getSex(), searchingObject.getAge())
                .filter(Objects::nonNull)
                .count();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (!StringUtils.isEmpty(searchingObject.getName())) {
            boolQueryBuilder.should(QueryBuilders.regexpQuery("name", wrapWithRegexp(searchingObject.getName())));
        }
        if (!StringUtils.isEmpty(searchingObject.getDescription())) {
            boolQueryBuilder.should(QueryBuilders.regexpQuery("description", wrapWithRegexp(searchingObject.getDescription())));
        }
        if (!StringUtils.isEmpty(searchingObject.getMaterial())) {
            boolQueryBuilder.should(QueryBuilders.regexpQuery("materials.material", wrapWithRegexp(searchingObject.getMaterial())));
        }
        if (!StringUtils.isEmpty(searchingObject.getSex())) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("sex", searchingObject.getSex()));
        }
        if (searchingObject.getAge() != null) {
            boolQueryBuilder.should(QueryBuilders.termQuery("lineSizes.size.age", searchingObject.getAge()));
        }
        if (!StringUtils.isEmpty(searchingObject.getHeight())) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("lineSizes.size.height", searchingObject.getHeight()));
        }
        if (!StringUtils.isEmpty(searchingObject.getColor())) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("lineSizes.colorAmount.color", searchingObject.getColor()));
        }
        boolQueryBuilder.minimumShouldMatch((int) countNonNullFields);

        NativeSearchQuery clothSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();

        return elasticsearchOperations.search(clothSearchQuery, cClass);
    }

    private String wrapWithRegexp(String value) {
        final String elasticRegex = ".*";

        return elasticRegex + value + elasticRegex;
    }
}
