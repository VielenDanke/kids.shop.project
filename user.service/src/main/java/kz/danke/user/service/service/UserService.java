package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Cart> processCartShop(Cart cart);
}
