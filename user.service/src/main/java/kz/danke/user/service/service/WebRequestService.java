package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import reactor.core.publisher.Mono;

public interface WebRequestService {

    Mono<Cart> reserveOrDeclineWebRequest(Cart cart, String url);
}
