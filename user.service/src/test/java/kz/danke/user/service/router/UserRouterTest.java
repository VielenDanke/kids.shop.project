package kz.danke.user.service.router;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(userService, jwtService, jsonObjectMapper, stateMachineProcessingService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void userRouter_GetUserCabinet() throws JsonProcessingException {
        final String id = UUID.randomUUID().toString();
        final User user = User.builder()
                .id(id)
                .username(testData)
                .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                .build();
        final Claims claims = new DefaultClaims(Collections.singletonMap("user", new ObjectMapper().writeValueAsString(user)));

        when(userService.getUserInSession()).thenReturn(Mono.just(user));
        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.extractTokenClaims(anyString())).thenReturn(claims);

        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", "token")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserCabinetResponse.class)
                .value(u -> Assertions.assertEquals(u.getId(), id));

        verify(userService, times(1)).getUserInSession();
    }
}
