package kz.danke.user.service.router;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.ClothCart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.request.UserUpdateRequest;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserRouterTest extends AbstractRouterLayer {

    @BeforeEach
    public void setup(ApplicationContext applicationContext) {
        Mockito.reset(userService, stateMachineProcessingService);
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    public void userRouter_UpdateUser() {
        String id = UUID.randomUUID().toString();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        User user = User.builder()
                .id(id)
                .username(testData)
                .firstName(testData)
                .lastName(testData)
                .phoneNumber(testData)
                .address(testData)
                .city(testData)
                .authorities(Collections.singleton(Authorities.ROLE_USER.name()))
                .build();
        String token = jwtService.generateToken(user);

        when(userService.updateUser(any(UserUpdateRequest.class))).thenReturn(Mono.just(user));

        webTestClient
                .post()
                .uri("/cabinet/update")
                .header("accessToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userUpdateRequest), UserUpdateRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("accessToken")
                .expectHeader().exists(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)
                .expectBody(UserCabinetResponse.class)
                .value(userCabinetResponse -> {
                    Assertions.assertEquals(testData, userCabinetResponse.getUsername());
                    Assertions.assertEquals(testData, userCabinetResponse.getFirstName());
                    Assertions.assertEquals(testData, userCabinetResponse.getLastName());
                    Assertions.assertEquals(testData, userCabinetResponse.getPhoneNumber());
                    Assertions.assertEquals(testData, userCabinetResponse.getAddress());
                    Assertions.assertEquals(testData, userCabinetResponse.getCity());
                });

        verify(userService, times(1)).updateUser(any(UserUpdateRequest.class));
    }

    @Test
    public void userRouter_UpdateUser_ReturnsNotAuthorizedException() throws JsonProcessingException {
        UserUpdateRequest request = new UserUpdateRequest();
        String token = TestUtil.generateToken(User.builder().build(), null, properties.getJwt().getSecret());

        webTestClient
                .post()
                .uri("/cabinet/update")
                .header("accessToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserUpdateRequest.class)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).updateUser(any(UserUpdateRequest.class));
    }

    @Test
    public void userRouter_UpdateUser_ReturnsNotAuthorizedException_EmptyHeader() {
        webTestClient
                .post()
                .uri("/cabinet/update")
                .body(Mono.just(new UserUpdateRequest()), UserUpdateRequest.class)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).updateUser(any(UserUpdateRequest.class));
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
    public void userRouter_GetUserCabinet_ReturnUserNotAuthorizedException() throws JsonProcessingException {
        User user = User.builder().id(UUID.randomUUID().toString()).build();

        String token = TestUtil.generateToken(user, null, properties.getJwt().getSecret());

        webTestClient
                .get()
                .uri("/cabinet")
                .header("accessToken", token)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).getUserInSession();
    }

    @Test
    public void userRouter_GetUserCabinet_ReturnUserNotAuthorizedException_EmptyHeader() {
        webTestClient
                .get()
                .uri("/cabinet")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(0)).getUserInSession();
    }

    @Test
    public void userRouter_ReserveUserCart() {
        final String testID = UUID.randomUUID().toString();
        final List<ClothCart> clothCartList = new ArrayList<>() {{
            add(new ClothCart(testID, testNumber, testData, testNumber, testNumber));
        }};
        final Cart cart = Cart.builder().clothCartList(clothCartList).build();

        doNothing().when(stateMachineProcessingService).processReserve(any(Cart.class), anyString());

        webTestClient
                .post()
                .uri("/cart/reserve")
                .body(Mono.just(cart), Cart.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectHeader().exists("STATE_ID")
                .expectHeader().exists(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)
                .expectStatus().isOk()
                .expectBody(String.class);

        verify(stateMachineProcessingService, times(1)).processReserve(any(Cart.class), anyString());
    }

    @Test
    public void userRouter_CartReserveDecline() {
        final String stateID = UUID.randomUUID().toString();

        doNothing().when(stateMachineProcessingService).processReserveDecline(anyString());

        webTestClient
                .post()
                .uri("/cart/reserve/decline")
                .header("STATE_ID", stateID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(r -> Assertions.assertTrue(r.contains(stateID)));

        verify(stateMachineProcessingService, times(1)).processReserveDecline(anyString());
    }

    @Test
    public void userRouter_CartRetrieve() {
        final String testID = UUID.randomUUID().toString();
        final ClothCart testClothCart = new ClothCart(testID, testNumber, testData, testNumber, testNumber);
        final List<ClothCart> clothCartList = new ArrayList<>() {{
            add(testClothCart);
        }};
        final Cart cart = Cart.builder().clothCartList(clothCartList).build();

        when(stateMachineProcessingService.retrieveCartFromStateMachine(anyString()))
                .thenReturn(cart);

        webTestClient
                .post()
                .uri("/cart/retrieve")
                .header("STATE_ID", testID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Cart.class)
                .value(c -> {
                    List<ClothCart> cList = c.getClothCartList();
                    Assertions.assertTrue(cList.contains(testClothCart));
                });

        verify(stateMachineProcessingService, times(1)).retrieveCartFromStateMachine(anyString());
    }

    @Test
    public void userRouter_CartProcessing() {
        final String testID = UUID.randomUUID().toString();
        final ChargeRequest request = new ChargeRequest(
                testData, testData, testData, testData, testData, testData
        );

        doNothing().when(stateMachineProcessingService).processChargeEvent(any(ChargeRequest.class), anyString());

        webTestClient
                .post()
                .uri("/cart/process")
                .body(Mono.just(request), ChargeRequest.class)
                .header("STATE_ID", testID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> Assertions.assertTrue(s.contains(testID)));

        verify(stateMachineProcessingService, times(1)).processChargeEvent(any(ChargeRequest.class), anyString());
    }
}
