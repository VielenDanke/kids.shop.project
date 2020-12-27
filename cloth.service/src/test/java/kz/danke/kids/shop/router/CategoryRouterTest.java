package kz.danke.kids.shop.router;

import kz.danke.kids.shop.document.Category;
import kz.danke.kids.shop.dto.request.CategorySaveRequest;
import kz.danke.kids.shop.exceptions.ResponseFailed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CategoryRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(categoryService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void categoryHandler_GetAllCategories() {
        final String testCategory = "category";
        final String testId = UUID.randomUUID().toString();
        final Category category = Category.builder().id(testId).category(testCategory).build();

        Mockito.when(categoryService.findAll()).thenReturn(Flux.just(category));

        webTestClient
                .get()
                .uri("/categories")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Category.class)
                .value(catResp -> {
                    boolean isMatch = catResp.stream().anyMatch(cat ->
                            cat.getId().equals(testId) && cat.getCategory().equals(testCategory)
                    );
                    Assertions.assertTrue(isMatch);
                });

        Mockito.verify(categoryService, Mockito.times(1)).findAll();
    }

    @Test
    public void categoryHandler_FindAllReturnException() {
        Mockito.when(categoryService.findAll())
                .thenReturn(Flux.from(Mono.defer(() -> Mono.error(new RuntimeException()))));

        webTestClient
                .get()
                .uri("/categories")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(categoryService, Mockito.times(1)).findAll();
    }

    @Test
    public void categoryHandler_AddCategory() {
        final String testCategory = "category";
        final String testCategoryId = UUID.randomUUID().toString();
        final CategorySaveRequest request = new CategorySaveRequest(testCategory);
        final Category category = Category.builder().id(testCategoryId).category(testCategory).build();

        Mockito.when(categoryService.save(Mockito.any(Category.class))).thenReturn(Mono.just(category));

        webTestClient
                .post()
                .uri("/categories")
                .body(Mono.just(request), CategorySaveRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Category.class)
                .value(cat -> Assertions.assertEquals(cat.getId(), testCategoryId));

        Mockito.verify(categoryService, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    public void categoryHandler_SaveCategoryReturnException() {
        final String testCategoryName = "category";
        final CategorySaveRequest request = new CategorySaveRequest(testCategoryName);

        Mockito.when(categoryService.save(Mockito.any(Category.class)))
                .thenReturn(Mono.defer(() -> Mono.error(new RuntimeException())));

        webTestClient
                .post()
                .uri("/categories")
                .body(Mono.just(request), CategorySaveRequest.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(categoryService, Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    public void categoryHandler_SaveCategoryBadRequest() {
        webTestClient
                .post()
                .uri("/categories")
                .body(Mono.just("invalid"), String.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ResponseFailed.class);

        Mockito.verify(categoryService, Mockito.times(0)).save(Mockito.any(Category.class));
    }
}
