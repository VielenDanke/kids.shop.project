package kz.danke.kids.shop.router;

import kz.danke.kids.shop.document.PromotionCard;
import kz.danke.kids.shop.dto.request.PromotionCardSaveRequest;
import kz.danke.kids.shop.exceptions.NotFoundException;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.Part;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

public class PromotionsRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(promotionService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void promotionHandler_FindAllPromotions() {
        final PromotionCard promotionCard = PromotionCard.builder().id(UUID.randomUUID().toString()).build();

        Mockito.when(promotionService.findAll()).thenReturn(Flux.just(promotionCard));

        webTestClient
                .get()
                .uri("/promotions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PromotionCard.class)
                .value(pc -> {
                    boolean isIdEquals = pc.stream().anyMatch(card -> card.getId().equals(promotionCard.getId()));
                    Assertions.assertTrue(isIdEquals);
                });

        Mockito.verify(promotionService, Mockito.times(1)).findAll();
    }

    @Test
    public void promotionHandler_FindAllPromotions_ReturnException() {
        Mockito.when(promotionService.findAll()).thenReturn(Flux.from(Mono.defer(() -> Mono.error(new RuntimeException()))));

        webTestClient
                .get()
                .uri("/promotions")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(promotionService, Mockito.times(1)).findAll();
    }

    @Test
    public void promotionHandler_SavePromotion() {
        final String testStr = "test";
        final PromotionCardSaveRequest request = new PromotionCardSaveRequest(testStr, testStr);
        final PromotionCard promotionCard = PromotionCard.builder()
                .id(UUID.randomUUID().toString()).name(testStr).description(testStr)
                .build();

        Mockito.when(promotionService.save(Mockito.any(PromotionCard.class))).thenReturn(Mono.just(promotionCard));

        webTestClient
                .post()
                .uri("/promotions")
                .body(Mono.just(request), PromotionCardSaveRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PromotionCard.class)
                .value(card -> {
                    Assertions.assertEquals(promotionCard.getId(), card.getId());
                    Assertions.assertEquals(promotionCard.getName(), card.getName());
                    Assertions.assertEquals(promotionCard.getDescription(), card.getDescription());
                });

        Mockito.verify(promotionService, Mockito.times(1)).save(Mockito.any(PromotionCard.class));
    }

    @Test
    public void promotionHandler_SavePromotion_ReturnException() {
        final String testStr = "test";
        final PromotionCardSaveRequest request = new PromotionCardSaveRequest(testStr, testStr);

        Mockito.when(promotionService.save(Mockito.any(PromotionCard.class)))
                .thenReturn(Mono.defer(() -> Mono.error(new RuntimeException())));

        webTestClient
                .post()
                .uri("/promotions")
                .body(Mono.just(request), PromotionCardSaveRequest.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(promotionService, Mockito.times(1)).save(Mockito.any(PromotionCard.class));
    }

    @Test
    public void promotionHandler_SavePromotion_BadRequest() {
        webTestClient
                .post()
                .uri("/promotions")
                .body(Mono.just("invalid"), String.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(promotionService, Mockito.times(0)).save(Mockito.any(PromotionCard.class));
    }

    @Test
    public void promotionHandler_DeletePromotion() {
        final String testId = UUID.randomUUID().toString();

        Mockito.when(promotionService.deletePromotionCardById(testId)).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/promotions/{id}", testId)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(promotionService, Mockito.times(1)).deletePromotionCardById(testId);
    }

    @Test
    public void promotionHandler_DeletePromotion_ReturnException() {
        final String testId = UUID.randomUUID().toString();

        Mockito.when(promotionService.deletePromotionCardById(testId))
                .thenReturn(Mono.defer(() -> Mono.error(new RuntimeException())));

        webTestClient
                .delete()
                .uri("/promotions/{id}", testId)
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(promotionService, Mockito.times(1)).deletePromotionCardById(testId);
    }

    @Test
    public void promotionHandler_AddFileToPromotion() throws Exception {
        final String testId = UUID.randomUUID().toString();
        final String test = "test";
        final PromotionCard promotionCard = PromotionCard.builder().id(testId).name(test).description(test).build();

        File tempFile = Files.createTempFile("test-file", ".jpg").toFile();
        FileCopyUtils.copy("image".getBytes(StandardCharsets.UTF_8), tempFile);
        Resource resource = new FileSystemResource(tempFile);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", resource);

        Mockito.when(promotionService.saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString()))
                .thenReturn(Mono.just(promotionCard));

        webTestClient
                .post()
                .uri("/promotions/{id}/file", testId)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PromotionCard.class);

        Mockito.verify(promotionService, Mockito.times(1))
                .saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString());
    }

    @Test
    public void promotionHandler_AddFileToCloth_ReturnException() throws Exception {
        final String testId = UUID.randomUUID().toString();

        File tempFile = Files.createTempFile("test-file", ".jpg").toFile();
        FileCopyUtils.copy("image".getBytes(StandardCharsets.UTF_8), tempFile);
        Resource resource = new FileSystemResource(tempFile);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", resource);

        Mockito.when(promotionService.saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString()))
                .thenReturn(Mono.defer(() -> Mono.error(new RuntimeException())));

        webTestClient
                .post()
                .uri("/promotions/{id}/file", testId)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(promotionService, Mockito.times(1))
                .saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString());
    }

    @Test
    public void promotionHandler_AddFileToCloth_PromotionCardNotFound() throws Exception {
        final String testId = UUID.randomUUID().toString();

        File tempFile = Files.createTempFile("test-file", ".jpg").toFile();
        FileCopyUtils.copy("image".getBytes(StandardCharsets.UTF_8), tempFile);
        Resource resource = new FileSystemResource(tempFile);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", resource);

        Mockito.when(promotionService.saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString()))
                .thenReturn(Mono.defer(() -> Mono.error(new NotFoundException("not found"))));

        webTestClient
                .post()
                .uri("/promotions/{id}/file", testId)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ResponseFailed.class);

        Mockito.verify(promotionService, Mockito.times(1))
                .saveFileToPromotionCard(Mockito.any(Part.class), Mockito.anyString());
    }

    @Test
    public void promotionHandler_AddFileToCloth_NoFilePassedIn() {
        webTestClient
                .post()
                .uri("/promotions/{id}/file", UUID.randomUUID().toString())
                .exchange()
                .expectStatus().is5xxServerError();

        Mockito.verify(clothService, Mockito.times(0)).addFilesToCloth(Mockito.anyList(), Mockito.anyString());
    }
}
