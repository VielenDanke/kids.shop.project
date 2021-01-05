package kz.danke.user.service.service.impl;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.exception.ClothCartNotFoundException;
import kz.danke.user.service.service.WebRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebRequestServiceImpl implements WebRequestService {

    private final WebClient webClient;

    @Autowired
    public WebRequestServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Cart> reserveOrDeclineWebRequest(Cart cart, String url) {
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
}
