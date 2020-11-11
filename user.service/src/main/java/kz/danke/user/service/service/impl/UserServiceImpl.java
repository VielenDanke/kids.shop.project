package kz.danke.user.service.service.impl;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Mono<Cart> processCartShop(Cart cart) {
        Mono<User> userMono = getPrincipalFromSecurityContext()
                .flatMap(reactiveUserRepository::findByUsername);

        Mono<Cart> cartMono = this.checkIfClothEnough(cart);

        return cartMono
                .zipWith(userMono)
                .flatMap(tuple -> {
                    Cart userCart = tuple.getT1();
                    User userInContext = tuple.getT2();

                    userInContext.setCart(userCart);

                    return reactiveUserRepository.save(userInContext);
                })
                .map(User::getCart);
    }

    private Mono<Cart> checkIfClothEnough(Cart cart) {
        return webClient
                .post()
                .uri("http://cloth-ms/clothes/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(cart), Cart.class)
                .retrieve()
                .bodyToMono(Cart.class);
    }

    private Mono<String> getPrincipalFromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotAuthorizedException("Security context not found"))))
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(String.class);
    }
}
