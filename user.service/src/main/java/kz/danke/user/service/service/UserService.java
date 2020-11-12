package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.dto.request.ChargeRequest;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Cart> validateCartShop(Cart cart);

    Mono<Integer> processCartShop(Cart cart, ChargeRequest chargeRequest);
}
