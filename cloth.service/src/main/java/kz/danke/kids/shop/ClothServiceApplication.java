package kz.danke.kids.shop;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class ClothServiceApplication {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;
    private final ClothService clothService;

    @Autowired
    public ClothServiceApplication(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl, ClothService clothService) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
        this.clothService = clothService;
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
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .name("first")
                                    .sex(Gender.MAN.name())
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
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .name("second")
                                    .sex(Gender.WOMAN.name())
                                    .lineSizes(Collections.singletonList(
                                            LineSize
                                                    .builder()
                                                    .age(2)
                                                    .height(Height.FORTY_FOUR.height())
                                                    .amount(7)
                                                    .build()
                                    ))
                                    .materials(Collections.singletonList(
                                            new Material("cotton", 80)
                                    ))
                                    .description("second description")
                                    .color("Grey")
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .name("third")
                                    .sex(Gender.MAN.name())
                                    .lineSizes(Collections.singletonList(
                                            LineSize
                                                    .builder()
                                                    .age(3)
                                                    .height(Height.FIFTY_SIX_FIRST.height())
                                                    .amount(4)
                                                    .build()
                                    ))
                                    .materials(Collections.singletonList(
                                            new Material("cotton", 70)
                                    ))
                                    .description("third description")
                                    .color("Green")
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .name("fourth")
                                    .sex(Gender.WOMAN.name())
                                    .lineSizes(Collections.singletonList(
                                            LineSize
                                                    .builder()
                                                    .age(7)
                                                    .height(Height.SEVENTY_TWO.height())
                                                    .amount(3)
                                                    .build()
                                    ))
                                    .materials(Collections.singletonList(
                                            new Material("polyester", 80)
                                    ))
                                    .description("fourth description")
                                    .color("Red")
                                    .build()
                    ))
                    .flatMap(clothReactiveElasticsearchRepositoryImpl::save)
                    .doOnNext(cloth -> System.out.println(cloth.getId()))
                    .blockLast();
        };
    }
}
