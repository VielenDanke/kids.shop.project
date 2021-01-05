package kz.danke.user.service.router;

import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(userService, jwtService, jsonObjectMapper, stateMachineProcessingService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void userRouter_GetUserCabinet() {
        final String id = UUID.randomUUID().toString();
        final User user = User.builder().id(id).build();

        when(userService.getUserInSession()).thenReturn(Mono.just(user));

        webTestClient
                .get()
                .uri("/cabinet")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserCabinetResponse.class)
                .value(u -> Assertions.assertEquals(u.getId(), id));

        verify(userService, times(1)).getUserInSession();
    }
}
