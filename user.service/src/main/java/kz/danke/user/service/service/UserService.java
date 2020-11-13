package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Cart> validateCartShop(Cart cart);

    Mono<ChargeResponse> processCartShop(ChargeRequest chargeRequest);
}
