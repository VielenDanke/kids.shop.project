package kz.danke.user.service.service.impl;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.request.UserUpdateRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.exception.UserNotFoundException;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.UserService;
import kz.danke.user.service.service.WebRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final ReactiveUserRepository reactiveUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final WebRequestService webRequestService;
    private final Environment environment;

    @Autowired
    public UserServiceImpl(ReactiveUserRepository reactiveUserRepository,
                           PasswordEncoder passwordEncoder,
                           WebRequestService webRequestService,
                           Environment environment) {
        this.reactiveUserRepository = reactiveUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.webRequestService = webRequestService;
        this.environment = environment;
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
        return this.getUserInSession()
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
        return webRequestService.reserveOrDeclineWebRequest(cart, environment.getProperty("app.url.reserve_cart"));
    }

    @Override
    public Mono<Cart> reserveDecline(Cart cart) {
        return webRequestService.reserveOrDeclineWebRequest(cart, environment.getProperty("app.url.decline_cart"));
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
