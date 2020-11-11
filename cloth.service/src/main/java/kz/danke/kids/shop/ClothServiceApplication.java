package kz.danke.kids.shop;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
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
            String filePath = "C:/Users/viele/IdeaProjects/Files/image.jpg";

            try (InputStream inputStream = new FileInputStream(filePath)) {

                clothReactiveElasticsearchRepositoryImpl
                        .deleteAll()
                        .thenMany(Flux.just(
                                Cloth.builder().id(UUID.randomUUID().toString())
                                        .name("name")
                                        .sex(Gender.MAN.name())
                                        .lineSizes(Collections.singletonList(
                                                LineSize
                                                        .builder()
                                                        .age(6)
                                                        .height(Height.THIRTY_SIX.height())
                                                        .color("Green")
                                                        .amount(5)
                                                        .build()
                                        ))
                                        .materials(Collections.singletonList(
                                                new Material("cotton", 80)
                                        ))
                                        .description("first").build(),
                                Cloth.builder().id(UUID.randomUUID().toString()).name("name")
                                        .materials(Collections.singletonList(
                                                new Material("cotton", 80)
                                        )).description("second").build(),
                                Cloth.builder().id(UUID.randomUUID().toString()).name("name")
                                        .materials(Collections.singletonList(
                                                new Material("cotton", 80)
                                        )).description("third").build(),
                                Cloth.builder().id(UUID.randomUUID().toString()).name("name")
                                        .materials(Collections.singletonList(
                                                new Material("cotton", 80)
                                        )).description("fourth").build()
                        ))
                        .flatMap(clothReactiveElasticsearchRepositoryImpl::save)
                        .doOnNext(cloth -> System.out.println(cloth.getId()))
                        .blockLast();
            }
        };
    }
}
