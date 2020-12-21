package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(initializers = AbstractRepositoryLayer.Initializer.class)
public abstract class AbstractRepositoryLayer {

    private static final String ELASTICSEARCH_DOCKER = "docker.elastic.co/elasticsearch/elasticsearch:7.6.2";

    @Autowired
    protected ClothReactiveElasticsearchRepositoryImpl clothRepository;
    @Autowired
    protected CategoryReactiveElasticsearchRepositoryImpl categoryRepository;
    @Autowired
    protected PromotionCardReactiveElasticsearchRepositoryImpl promotionRepository;
    @Autowired
    protected QueryCreator<Cloth, PublicSearchingObject> queryCreator;

    protected String testClothName = "first";
    protected String testClothMaterial = "cotton";
    protected String testClothDescription = "first description";
    protected String testClothColor = "Orange";
    protected int testClothPrice = 1200;
    protected String testClothCategory = "Jeans";
    protected String testPromotionName = "test promotion";
    protected String testPromotionDescription = "test description";
    protected String testCategoryName = "test category";

    @ClassRule
    public static ElasticsearchContainer container = new ElasticsearchContainer(ELASTICSEARCH_DOCKER);

    static {
        container.start();
    }

    protected Cloth testCloth;
    protected PromotionCard testPromotion;
    protected Category testCategory;

    {
        testCategory = Category.builder()
                .id(UUID.randomUUID().toString())
                .category(testCategoryName)
                .build();
        testPromotion = PromotionCard.builder()
                .id(UUID.randomUUID().toString())
                .name(testPromotionName)
                .description(testPromotionDescription)
                .build();
        testCloth = Cloth.builder()
                .id(UUID.randomUUID().toString())
                .images(Collections.emptyList())
                .name(testClothName)
                .gender(Gender.MAN.name())
                .lineSizes(Collections.singletonList(
                        LineSize
                                .builder()
                                .age(6)
                                .height(Height.THIRTY_SIX.height())
                                .amount(5)
                                .build()
                ))
                .materials(Collections.singletonList(
                        new Material(testClothMaterial, 80)
                ))
                .description(testClothDescription)
                .color(testClothColor)
                .price(testClothPrice)
                .category(testClothCategory)
                .build();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "app.elasticsearch.hostAndPort=" + container.getHttpHostAddress()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
