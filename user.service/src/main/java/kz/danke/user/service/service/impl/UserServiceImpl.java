package kz.danke.user.service.service.impl;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import kz.danke.user.service.exception.ClothCartNotFoundException;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.exception.UserNotFoundException;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;
    private final ReactiveUserRepository reactiveUserRepository;

    @Autowired
    public UserServiceImpl(WebClient webClient, ReactiveUserRepository reactiveUserRepository) {
        this.webClient = webClient;
        this.reactiveUserRepository = reactiveUserRepository;
    }

    @Override
    public Mono<User> getUserInSession() {
        return getPrincipalFromSecurityContext();
    }

    @Override
    public Mono<Cart> validateCartShop(Cart cart) {
        return getPrincipalFromSecurityContext()
                .then(this.getOnlyEnoughClothAmount(cart));
    }

    @Override
    public Mono<ChargeResponse> processCartShop(ChargeRequest chargeRequest) {
        return getPrincipalFromSecurityContext()
                .then(Mono.just(new ChargeResponse("Transaction complete successfully")));

    }

    private Mono<Cart> getOnlyEnoughClothAmount(Cart cart) {
        return webClient
                .post()
                .uri("http://cloth-ms/clothes/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(cart), Cart.class)
                .exchange()
                .flatMap(clientResponse -> {
                    HttpStatus httpStatus = clientResponse.statusCode();

                    if (!httpStatus.equals(HttpStatus.OK)) {
                        return Mono.defer(() -> Mono.error(new ClothCartNotFoundException(
                                "Cloth in cart not found", clientResponse.rawStatusCode()))
                        );
                    }
                    return clientResponse.bodyToMono(Cart.class);
                });
    }

    private Mono<User> getPrincipalFromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Security context not found"))))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(String.class)
                .flatMap(reactiveUserRepository::findByUsername)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException("User not found"))));
    }
}
