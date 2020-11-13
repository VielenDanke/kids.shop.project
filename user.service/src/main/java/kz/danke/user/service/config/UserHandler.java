package kz.danke.user.service.config;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import kz.danke.user.service.exception.ResponseFailed;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.exception.UserNotFoundException;
import kz.danke.user.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> handleCartProcess(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Cart.class)
                .flatMap(userService::validateCartShop)
                .flatMap(cart -> ServerResponse.ok().body(Mono.just(cart), Cart.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> ServerResponse
                        .status(401)
                        .body(Mono.just(new ResponseFailed(
                                ex.toString(),
                                ex.getLocalizedMessage(),
                                serverRequest.path())
                        ), ResponseFailed.class)
                )
                .onErrorResume(UserNotFoundException.class, ex -> ServerResponse
                        .status(400)
                        .body(Mono.just(new ResponseFailed(
                                ex.toString(),
                                ex.getLocalizedMessage(),
                                serverRequest.path())
                        ), ResponseFailed.class)
                );
    }

    public Mono<ServerResponse> handleChargeProcess(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ChargeRequest.class)
                .flatMap(userService::processCartShop)
                .flatMap(chargeResponse -> ServerResponse.ok().body(Mono.just(chargeResponse), ChargeResponse.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> ServerResponse
                        .status(401)
                        .body(Mono.just(new ResponseFailed(
                                ex.toString(),
                                ex.getLocalizedMessage(),
                                serverRequest.path())
                        ), ResponseFailed.class)
                )
                .onErrorResume(UserNotFoundException.class, ex -> ServerResponse
                        .status(400)
                        .body(Mono.just(new ResponseFailed(
                                ex.toString(),
                                ex.getLocalizedMessage(),
                                serverRequest.path())
                        ), ResponseFailed.class)
                );
    }
}
