package kz.danke.user.service.config;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.exception.ClothCartNotFoundException;
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
                .onErrorResume(ClothCartNotFoundException.class, ex ->
                        createServerResponse(ex, ex.getResponseStatus(), serverRequest)
                )
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    public Mono<ServerResponse> handleChargeProcess(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ChargeRequest.class)
                .flatMap(userService::processCartShop)
                .flatMap(chargeResponse -> ServerResponse.ok().body(Mono.just(chargeResponse), ChargeResponse.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    private Mono<ServerResponse> createServerResponse(Exception ex, Integer status, ServerRequest request) {
        return ServerResponse
                .status(status)
                .body(Mono.just(new ResponseFailed(
                        ex.toString(),
                        ex.getLocalizedMessage(),
                        request.path())
                ), ResponseFailed.class);
    }

    public Mono<ServerResponse> getUserCabinet(ServerRequest serverRequest) {
        return userService.getUserInSession()
                .map(UserCabinetResponse::toUserCabinetResponse)
                .flatMap(user -> ServerResponse.ok().body(Mono.just(user), UserCabinetResponse.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }
}
