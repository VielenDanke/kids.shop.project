package kz.danke.user.service.router;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.exception.ResponseFailed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(userService, stateMachineProcessingService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void userRouter_GetUserCabinet() {
        final String id = UUID.randomUUID().toString();
        final User user = User.builder()
                .id(id)
                .username(testData)
                .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                .build();
        String token = jwtService.generateToken(user);

        when(userService.getUserInSession()).thenReturn(Mono.just(user));

        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserCabinetResponse.class)
                .value(u -> Assertions.assertEquals(u.getId(), id));

        verify(userService, times(1)).getUserInSession();
    }

    @Test
    public void userRouter_GetUserCabinet_ReturnUserNotAuthorizedException() {
        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", "token")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ResponseFailed.class);

        verify(userService, times(0)).getUserInSession();
    }
}
