package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import kz.danke.kids.shop.service.searching.QueryCreator;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.net.InetSocketAddress;
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
    protected QueryCreator<Cloth, PublicSearchingObject> queryCreator;

    @ClassRule
    public static ElasticsearchContainer container = new ElasticsearchContainer(ELASTICSEARCH_DOCKER);

    static {
        container.start();
    }

    protected Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString())
            .images(Collections.emptyList())
            .name("first")
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
                    new Material("cotton", 80)
            ))
            .description("first description")
            .color("Orange")
            .price(1200)
            .category("Jeans")
            .build();

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "app.elasticsearch.hostAndPort=" + container.getHttpHostAddress()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
