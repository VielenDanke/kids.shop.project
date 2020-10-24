package kz.danke.kids.shop;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class ClothServiceApplication {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;

    @Autowired
    public ClothServiceApplication(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClothServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            clothReactiveElasticsearchRepositoryImpl
                    .deleteAll()
                    .thenMany(Flux.just(
                            Cloth.builder().id(UUID.randomUUID().toString()).description("first").build(),
                            Cloth.builder().id(UUID.randomUUID().toString()).description("second").build(),
                            Cloth.builder().id(UUID.randomUUID().toString()).description("third").build(),
                            Cloth.builder().id(UUID.randomUUID().toString()).description("fourth").build()
                    ))
                    .flatMap(clothReactiveElasticsearchRepositoryImpl::save)
                    .doOnNext(cloth -> System.out.println(cloth.getId()))
                    .blockLast();
        };
    }
}
