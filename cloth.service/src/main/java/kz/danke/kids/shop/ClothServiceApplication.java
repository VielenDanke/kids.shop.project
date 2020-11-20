package kz.danke.kids.shop;

import kz.danke.kids.shop.config.AppConfigProperties;
import kz.danke.kids.shop.document.*;
import kz.danke.kids.shop.repository.CategoryReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.ClothReactiveElasticsearchRepositoryImpl;
import kz.danke.kids.shop.repository.PromotionCartReactiveElasticsearchRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class ClothServiceApplication {

    private final ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl;
    private final CategoryReactiveElasticsearchRepositoryImpl categoryRepository;
    private final PromotionCartReactiveElasticsearchRepositoryImpl promotionRepository;

    @Autowired
    public ClothServiceApplication(ClothReactiveElasticsearchRepositoryImpl clothReactiveElasticsearchRepositoryImpl,
                                   CategoryReactiveElasticsearchRepositoryImpl categoryRepository,
                                   PromotionCartReactiveElasticsearchRepositoryImpl promotionRepository) {
        this.clothReactiveElasticsearchRepositoryImpl = clothReactiveElasticsearchRepositoryImpl;
        this.categoryRepository = categoryRepository;
        this.promotionRepository = promotionRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClothServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() throws InterruptedException, IOException {
        File file = new File("image.jpg");

        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] fileBytes = fileInputStream.readAllBytes();

        final String image = Base64.getEncoder().encodeToString(fileBytes);

        return args -> {
            promotionRepository
                    .deleteAll()
                    .thenMany(Flux.fromIterable(Arrays.asList(
                            PromotionCard.builder().image(image).name("first").description("first").id(UUID.randomUUID().toString()).build(),
                            PromotionCard.builder().image(image).name("second").description("second").id(UUID.randomUUID().toString()).build(),
                            PromotionCard.builder().image(image).name("third").description("third").id(UUID.randomUUID().toString()).build()
                    )))
                    .flatMap(promotionRepository::save)
                    .doOnNext(prCadr -> System.out.println("PrCadr: " + prCadr.getId()))
                    .then(categoryRepository.deleteAll())
                    .thenMany(Flux.fromIterable(Arrays.asList(
                            Category.builder().id(UUID.randomUUID().toString()).category("Jeans").build(),
                            Category.builder().id(UUID.randomUUID().toString()).category("Jacket").build(),
                            Category.builder().id(UUID.randomUUID().toString()).category("Shirt").build(),
                            Category.builder().id(UUID.randomUUID().toString()).category("Cap").build()
                    )))
                    .flatMap(categoryRepository::save)
                    .doOnNext(cat -> System.out.println("Category: " + cat.getId()))
                    .then(clothReactiveElasticsearchRepositoryImpl.deleteAll())
                    .thenMany(Flux.just(
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .images(Collections.singletonList(image))
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
                                    .price(1200)
                                    .category("Jeans")
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .images(Collections.singletonList(image))
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
                                    .price(2500)
                                    .category("Shirt")
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .images(Collections.singletonList(image))
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
                                    .price(3700)
                                    .category("Jacket")
                                    .build(),
                            Cloth.builder().id(UUID.randomUUID().toString())
                                    .images(Collections.singletonList(image))
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
                                    .category("Cap")
                                    .price(4500)
                                    .build()
                    ))
                    .flatMap(clothReactiveElasticsearchRepositoryImpl::save)
                    .doOnNext(cloth -> System.out.println("Cloth: " + cloth.getId()))
                    .blockLast();
        };
    }
}
