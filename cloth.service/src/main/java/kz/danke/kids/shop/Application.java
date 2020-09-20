package kz.danke.kids.shop;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class Application {

    private final ClothReactiveElasticsearchRepository clothReactiveElasticsearchRepository;

    @Autowired
    public Application(ClothReactiveElasticsearchRepository clothReactiveElasticsearchRepository) {
        this.clothReactiveElasticsearchRepository = clothReactiveElasticsearchRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            clothReactiveElasticsearchRepository
                    .deleteAll()
                    .block();

            Cloth first = Cloth.builder().id(UUID.randomUUID().toString()).description("first").build();
            Cloth second = Cloth.builder().id(UUID.randomUUID().toString()).description("second").build();
            Cloth third = Cloth.builder().id(UUID.randomUUID().toString()).description("third").build();
            Cloth fourth = Cloth.builder().id(UUID.randomUUID().toString()).description("fourth").build();

            clothReactiveElasticsearchRepository
                    .saveAll(Arrays.asList(first, second, third, fourth))
                    .blockLast();
        };
    }
}
