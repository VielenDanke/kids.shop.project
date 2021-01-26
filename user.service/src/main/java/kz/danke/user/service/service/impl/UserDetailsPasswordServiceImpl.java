package kz.danke.user.service.service.impl;

import kz.danke.user.service.config.security.UserDetailsImpl;
import kz.danke.user.service.exception.UserNotFoundException;
import kz.danke.user.service.repository.ReactiveUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsPasswordServiceImpl implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    private final ReactiveUserRepository reactiveUserRepository;

    @Autowired
    public UserDetailsPasswordServiceImpl(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return reactiveUserRepository.findByUsername(username)
                .map(UserDetailsImpl::new);
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, String newPassword) {
        return reactiveUserRepository.findByUsername(userDetails.getUsername())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException("User not found"))))
                .doOnNext(user -> user.setPassword(newPassword))
                .flatMap(reactiveUserRepository::save)
                .map(UserDetailsImpl::new);
    }
}
