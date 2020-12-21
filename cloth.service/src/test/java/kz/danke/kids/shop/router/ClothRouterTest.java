package kz.danke.kids.shop.router;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.ClothDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class ClothRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void clothHandler_FindAllClothes() {
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(clothService.findAll()).thenReturn(Flux.just(cloth));

        webTestClient
                .get()
                .uri("/clothes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClothDTO.class)
                .value(clothResponse -> {
                    Assertions.assertEquals(clothResponse.get(0).getId(), cloth.getId());
                });
    }

    @Test
    public void clothHandler_FindClothById() {
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(clothService.findById(cloth.getId())).thenReturn(Mono.just(cloth));

        webTestClient
                .get()
                .uri("/clothes/{id}", cloth.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cloth.class)
                .value(clothResponse -> {
                    Assertions.assertNotNull(clothResponse);
                    Assertions.assertEquals(clothResponse.getId(), cloth.getId());
                });
    }
}
