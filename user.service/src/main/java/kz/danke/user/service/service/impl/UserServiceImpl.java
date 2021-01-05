package kz.danke.user.service.service.impl;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.request.UserUpdateRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;
    private final ReactiveUserRepository reactiveUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppConfigProperties properties;

    @Autowired
    public UserServiceImpl(WebClient webClient,
                           ReactiveUserRepository reactiveUserRepository,
                           PasswordEncoder passwordEncoder,
                           AppConfigProperties properties) {
        this.webClient = webClient;
        this.reactiveUserRepository = reactiveUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public Mono<User> saveNewUser(User user) {
        return Mono.just(user)
                .doOnNext(u -> {
                    u.setId(UUID.randomUUID().toString());
                    u.setPassword(passwordEncoder.encode(u.getPassword()));
                    u.setAuthorities(Collections.singleton(Authorities.ROLE_USER.name()));
                })
                .flatMap(reactiveUserRepository::save);
    }

    @Override
    public Mono<User> updateUser(UserUpdateRequest request) {
        return this.getPrincipalFromSecurityContext()
                .doOnNext(user -> {
                    user.setAddress(request.getAddress());
                    user.setPhoneNumber(request.getPhoneNumber());
                    user.setCity(request.getCity());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setUsername(request.getUsername());
                })
                .flatMap(reactiveUserRepository::save);
    }

    @Override
    public Mono<User> getUserInSession() {
        return getPrincipalFromSecurityContext();
    }

    @Override
    public Mono<Cart> reserveCartShop(Cart cart) {
        return this.reserveOrDeclineWebRequest(cart, properties.getUrl().getReserveCart());
    }

    @Override
    public Mono<ChargeResponse> processCartShop(ChargeRequest chargeRequest) {
        return getPrincipalFromSecurityContext()
                .then(Mono.just(new ChargeResponse("Transaction complete successfully")));

    }

    @Override
    public Mono<Cart> reserveDecline(Cart cart) {
        return this.reserveOrDeclineWebRequest(cart, properties.getUrl().getDeclineCart());
    }

    private Mono<Cart> reserveOrDeclineWebRequest(Cart cart, String url) {
        return webClient
                .post()
                .uri(url)
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
