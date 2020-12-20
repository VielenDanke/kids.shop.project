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

    public static void main(String[] args) {
        SpringApplication.run(ClothServiceApplication.class, args);
    }
}
