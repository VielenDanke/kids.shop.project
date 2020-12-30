package kz.danke.kids.shop.router;

import kz.danke.kids.shop.document.Cart;
import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.document.ClothCart;
import kz.danke.kids.shop.document.LineSize;
import kz.danke.kids.shop.dto.ClothDTO;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.exceptions.ClothNotFoundException;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import kz.danke.kids.shop.service.searching.PublicSearchingObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ClothRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(clothService);
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
                .value(clothResponse -> Assertions.assertEquals(clothResponse.get(0).getId(), cloth.getId()));

        Mockito.verify(clothService, Mockito.times(1)).findAll();
    }

    @Test
    public void clothHandler_FindAllClothes_ReturnException() {
        Mockito.when(clothService.findAll())
                .thenReturn(Flux.from(Mono.defer(() -> Mono.error(new RuntimeException("Test error")))));

        webTestClient
                .get()
                .uri("/clothes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(clothService, Mockito.times(1)).findAll();
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

        Mockito.verify(clothService, Mockito.times(1)).findById(cloth.getId());
    }

    @Test
    public void clothHandler_FindClothById_ReturnException() {
        Mockito.when(clothService.findById(Mockito.anyString()))
                .thenReturn(Mono.defer(() -> Mono.error(new ClothNotFoundException("test"))));

        webTestClient
                .get()
                .uri("/clothes/{id}", UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(clothService, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void clothHandler_SaveNewCloth() {
        final String testClothName = UUID.randomUUID().toString();
        final ClothSaveRequest request = ClothSaveRequest.builder().name(testClothName).build();
        final Cloth cloth = Cloth.builder().name(testClothName).build();

        Mockito.when(clothService.save(cloth)).thenReturn(Mono.just(cloth));

        webTestClient
                .post()
                .uri("/clothes")
                .body(Mono.just(request), ClothSaveRequest.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Cloth.class)
                .value(clothResponse -> {
                    Assertions.assertNotNull(clothResponse);
                    Assertions.assertEquals(clothResponse.getName(), testClothName);
                });

        Mockito.verify(clothService, Mockito.times(1)).save(cloth);
    }

    @Test
    public void clothHandler_SaveNewCloth_ReturnException() {
        ClothSaveRequest request = ClothSaveRequest.builder().name("Test").build();
        Cloth cloth = Cloth.builder().build();

        Mockito.when(clothService.save(cloth))
                .thenReturn(Mono.defer(() -> Mono.error(new RuntimeException("Test exception"))));

        webTestClient
                .post()
                .uri("/clothes")
                .body(Mono.just(request), ClothSaveRequest.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(clothService, Mockito.times(1)).save(cloth);
    }

    @Test
    public void clothHandler_DeleteById() {
        final String testId = UUID.randomUUID().toString();

        Mockito.when(clothService.deleteById(testId)).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/clothes/{id}", testId)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(clothService, Mockito.times(1)).deleteById(testId);
    }

    @Test
    public void clothHandler_DeleteById_ReturnException() {
        Mockito.when(clothService.deleteById(Mockito.anyString()))
                .thenReturn(Mono.defer(() -> Mono.error(new ClothNotFoundException("Test"))));

        webTestClient
                .delete()
                .uri("/clothes/{id}", UUID.randomUUID().toString())
                .exchange()
                .expectStatus().is4xxClientError();

        Mockito.verify(clothService, Mockito.times(1)).deleteById(Mockito.anyString());
    }

    @Test
    public void clothHandler_GetClothesCart() {
        Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(clothService.findByIdIn(cloth.getId())).thenReturn(Flux.just(cloth));

        webTestClient
                .post()
                .uri("/clothes/cart")
                .body(
                        Mono.just(Collections.singletonList(cloth.getId())),
                        new ParameterizedTypeReference<>() {}
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Cloth.class);

        Mockito.verify(clothService, Mockito.times(1)).findByIdIn(cloth.getId());
    }

    @Test
    public void clothHandler_GetClothesCart_ReturnException() {
        final String testId = UUID.randomUUID().toString();

        Mockito.when(clothService.findByIdIn(testId))
                .thenReturn(Flux.from(Mono.defer(() -> Mono.error(new RuntimeException()))));

        webTestClient
                .post()
                .uri("/clothes/cart")
                .body(
                        Mono.just(Collections.singletonList(testId)),
                        new ParameterizedTypeReference<>() {}
                )
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(clothService, Mockito.times(1)).findByIdIn(testId);
    }

    @Test
    public void clothHandler_HandleFileSavingByClothId() throws IOException {
        File tempFile = Files.createTempFile("test-file", ".jpg").toFile();
        FileCopyUtils.copy("image".getBytes(StandardCharsets.UTF_8), tempFile);
        Resource resource = new FileSystemResource(tempFile);

        final Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", resource);

        Mockito.when(clothService.addFilesToCloth(Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.just(cloth));

        webTestClient
                .post()
                .uri("/clothes/{id}/files", cloth.getId())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClothDTO.class);

        Mockito.verify(clothService, Mockito.times(1)).addFilesToCloth(Mockito.anyList(), Mockito.anyString());
    }

    @Test
    public void clothHandler_HandleFileSavingByClothId_ReturnException() {
        webTestClient
                .post()
                .uri("/clothes/{id}/files", UUID.randomUUID().toString())
                .exchange()
                .expectStatus().is4xxClientError();

        Mockito.verify(clothService, Mockito.times(0)).addFilesToCloth(Mockito.anyList(), Mockito.anyString());
    }

    @Test
    public void clothHandler_HandleFileSavingByClothId_ClothNotFoundException() throws Exception {
        File tempFile = Files.createTempFile("test-file", ".jpg").toFile();
        FileCopyUtils.copy("image".getBytes(StandardCharsets.UTF_8), tempFile);
        Resource resource = new FileSystemResource(tempFile);

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", resource);

        Mockito.when(clothService.addFilesToCloth(Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Mono.defer(() -> Mono.error(new ClothNotFoundException("Test"))));

        webTestClient
                .post()
                .uri("/clothes/{id}/files", UUID.randomUUID().toString())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ResponseFailed.class);

        Mockito.verify(clothService, Mockito.times(1)).addFilesToCloth(Mockito.anyList(), Mockito.anyString());
    }

    @Test
    public void clothHandler_HandleClothSearchingByPublicSearchingObject() {
        final PublicSearchingObject publicSearchingObject = new PublicSearchingObject();
        final Cloth cloth = Cloth.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(clothService.findAllTextSearching(Mockito.any(PublicSearchingObject.class)))
                .thenReturn(Flux.just(cloth));

        webTestClient
                .post()
                .uri("/clothes/searching")
                .body(Mono.just(publicSearchingObject), PublicSearchingObject.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClothDTO.class);

        Mockito.verify(clothService, Mockito.times(1))
                .findAllTextSearching(Mockito.any(PublicSearchingObject.class));
    }

    @Test
    public void clothHandler_HandleClothSearchingByPublicSearchingObject_ReturnException() {
        webTestClient
                .post()
                .uri("/clothes/searching")
                .body(Mono.just("invalid"), String.class)
                .exchange()
                .expectStatus().isBadRequest();

        Mockito.verify(clothService, Mockito.times(0)).findAllTextSearching(Mockito.any());
    }

    @Test
    public void clothHandler_HandleClothReserveCart() {
        final String clothId = UUID.randomUUID().toString();
        final String testHeight = "height";
        final int testInt = 5;
        final Cloth cloth = Cloth.builder()
                .id(clothId)
                .lineSizes(new ArrayList<>(
                        Collections.singletonList(LineSize.builder().age(5).height(testHeight).amount(5).build()))
                )
                .build();
        final List<ClothCart> clothCartList = new ArrayList<>() {{
            add(new ClothCart(clothId, testInt, testHeight, testInt, testInt));
        }};
        final Cart cart = new Cart(clothCartList);

        Mockito.when(clothService.findByIdIn(Mockito.anyString())).thenReturn(Flux.just(cloth));
        Mockito.when(clothService.saveWithoutSetId(Mockito.any(Cloth.class))).thenReturn(Mono.just(cloth));

        webTestClient
                .post()
                .uri("/clothes/reserve")
                .body(Mono.just(cart), Cart.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cart.class)
                .value(responseCart -> {
                    List<ClothCart> responseClothCartList = responseCart.getClothCartList();
                    Assertions.assertTrue(responseClothCartList.stream().anyMatch(cc -> cc.getId().equals(clothId)));
                });

        Mockito.verify(clothService, Mockito.times(1)).findByIdIn(Mockito.anyString());
        Mockito.verify(clothService, Mockito.times(1)).saveWithoutSetId(Mockito.any(Cloth.class));
    }

    @Test
    public void clothHandler_HandleDeclineReserveCart() {
        final String clothId = UUID.randomUUID().toString();
        final String testHeight = "height";
        final int testInt = 5;
        final Cloth cloth = Cloth.builder()
                .id(clothId)
                .lineSizes(new ArrayList<>(
                        Collections.singletonList(LineSize.builder().age(5).height(testHeight).amount(5).build()))
                )
                .build();
        final List<ClothCart> clothCartList = new ArrayList<>() {{
            add(new ClothCart(clothId, testInt, testHeight, testInt, testInt));
        }};
        final Cart cart = new Cart(clothCartList);

        Mockito.when(clothService.findByIdIn(Mockito.anyString())).thenReturn(Flux.just(cloth));
        Mockito.when(clothService.saveWithoutSetId(Mockito.any(Cloth.class))).thenReturn(Mono.just(cloth));

        webTestClient
                .post()
                .uri("/clothes/reserve/decline")
                .body(Mono.just(cart), Cart.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cart.class)
                .value(responseCart -> {
                    List<ClothCart> responseClothCartList = responseCart.getClothCartList();
                    Assertions.assertTrue(responseClothCartList.stream().anyMatch(cc -> cc.getId().equals(clothId)));
                });

        Mockito.verify(clothService, Mockito.times(1)).findByIdIn(Mockito.anyString());
        Mockito.verify(clothService, Mockito.times(1)).saveWithoutSetId(Mockito.any(Cloth.class));
    }
}