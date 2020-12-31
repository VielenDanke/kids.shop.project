package kz.danke.user.service.repository;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(initializers = AbstractRepositoryLayer.Initializer.class)
public abstract class AbstractRepositoryLayer {

    private static final String ELASTICSEARCH_DOCKER = "docker.elastic.co/elasticsearch/elasticsearch:7.6.2";

    @Autowired
    protected ReactiveUserRepository userRepository;
    @Autowired
    protected ReactiveElasticsearchClient reactiveElasticsearchClient;

    protected String testData = "test";

    protected User testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .address(testData)
                .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                .city(testData)
                .firstName(testData)
                .lastName(testData)
                .password(testData)
                .phoneNumber(testData)
                .username(testData)
                .build();

    @ClassRule
    public static ElasticsearchContainer container = new ElasticsearchContainer(ELASTICSEARCH_DOCKER);

    static {
        container.start();
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
