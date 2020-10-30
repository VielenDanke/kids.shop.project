package kz.danke.edge.service.service;

import kz.danke.edge.service.document.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> save(User user);
}
