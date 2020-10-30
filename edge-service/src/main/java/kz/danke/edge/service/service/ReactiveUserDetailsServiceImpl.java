package kz.danke.edge.service.service;

import kz.danke.edge.service.configuration.security.UserDetailsImpl;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService, UserService {

    private final ReactiveUserRepository reactiveUserRepository;

    @Autowired
    public ReactiveUserDetailsServiceImpl(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return Mono.empty();
        }
        return reactiveUserRepository.findByUsername(username)
                .map(UserDetailsImpl::buildUserDetails);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, String newPassword) {
        return reactiveUserRepository.findByUsername(userDetails.getUsername())
                .doOnSuccess(user -> user.setPassword(newPassword))
                .flatMap(reactiveUserRepository::save)
                .map(UserDetailsImpl::buildUserDetails);
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.just(user)
                .doOnNext(usr -> {
                    usr.setAuthorities(Collections.singleton(Authorities.ROLE_USER.name()));
                    usr.setId(UUID.randomUUID().toString());
                })
                .flatMap(reactiveUserRepository::save);
    }
}
