package kz.danke.kids.shop.repository;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@Import({AbstractRepositoryLayer.ElasticsearchConfig.class, AppConfigProperties.class})
public abstract class AbstractRepositoryLayer {

    @Autowired
    protected ClothReactiveElasticsearchRepositoryImpl clothRepository;
    @Autowired
    protected AppConfigProperties properties;

    @Value("${test.containers.elasticsearch.docker_image_name}")
    private String elasticsearchDockerImageName;

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

    protected void setupElasticsearchTestContainer() {
        try (ElasticsearchContainer container = new ElasticsearchContainer(elasticsearchDockerImageName)) {
            container.start();
        }
    }

    @Configuration
    @EnableReactiveElasticsearchRepositories
    static class ElasticsearchConfig {

        @Value("${app.elasticsearch.hostAndPor}")
        private String hostAndPort;
        @Value("${app.elasticsearch.username}")
        private String username;
        @Value("${app.elasticsearch.password}")
        private String password;

        @Bean("reactiveElasticsearchTemplate")
        public ReactiveElasticsearchOperations reactiveElasticsearchOperations(
                ReactiveElasticsearchClient reactiveElasticsearchClient,
                @Qualifier("mappingElasticsearchConverter") ElasticsearchConverter elasticsearchConverter
        ) {
            return new ReactiveElasticsearchTemplate(reactiveElasticsearchClient, elasticsearchConverter);
        }

        @Bean("mappingElasticsearchConverter")
        public ElasticsearchConverter mappingElasticsearchConverter(
                @Qualifier("mappingContext") SimpleElasticsearchMappingContext mappingContext) {
            return new MappingElasticsearchConverter(mappingContext);
        }

        @Bean("mappingContext")
        public SimpleElasticsearchMappingContext mappingContext() {
            return new SimpleElasticsearchMappingContext();
        }

        @Bean("reactiveElasticsearchClient")
        public ReactiveElasticsearchClient reactiveElasticsearchClient() {
            ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(hostAndPort)
//                .usingSsl(Objects.requireNonNull(generateSslContext()))
                    .withBasicAuth(username, password)
                    .withWebClientConfigurer(webClient -> {
                        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                                .build();

                        return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
                    })
                    .build();

            return ReactiveRestClients.create(clientConfiguration);
        }
    }
}
