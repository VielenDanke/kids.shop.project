package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> saveNewUser(User user);

    Mono<Cart> reserveCartShop(Cart cart);

    Mono<Cart> reserveDecline(Cart cart);

    Mono<ChargeResponse> processCartShop(ChargeRequest chargeRequest);

    Mono<User> getUserInSession();
}
