package kz.danke.user.service.repository;

import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(initializers = AbstractRepositoryLayer.Initializer.class)
public abstract class AbstractRepositoryLayer {

    private static final String ELASTICSEARCH_DOCKER = "docker.elastic.co/elasticsearch/elasticsearch:7.6.2";

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
